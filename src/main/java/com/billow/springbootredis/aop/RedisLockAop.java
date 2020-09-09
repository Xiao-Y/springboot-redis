package com.billow.springbootredis.aop;

import com.billow.springbootredis.annotations.RedisLockable;
import com.billow.springbootredis.util.RedisLockUtil;
import com.google.common.base.Joiner;
import io.lettuce.core.RedisClient;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

import static com.billow.springbootredis.util.RedisLockUtil.unLock2;

@Aspect
@Component
public class RedisLockAop {

    @Resource
    private RedisClient redisClient;

    @Pointcut("execution(* com.billow.springbootredis.service.*.*(..))")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object doAround(ProceedingJoinPoint point) throws Throwable {
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        String targetName = point.getTarget().getClass().getName();
        String methodName = point.getSignature().getName();
        Object[] arguments = point.getArgs();

        if (method != null && method.isAnnotationPresent(RedisLockable.class)) {
            RedisLockable redisLock = method.getAnnotation(RedisLockable.class);
            long expire = redisLock.expiration();
            String redisKey = getLockKey(targetName, methodName, redisLock.key(), arguments);
            boolean isLock = RedisLockUtil.lock2(redisKey, expire);
            if (!isLock) {
                try {
                    return point.proceed();
                } finally {
                    unLock2(redisKey);
                }
            } else {
                throw new RuntimeException("您的操作太频繁，请稍后再试");
            }
        }

        return point.proceed();
    }

    private String getLockKey(String targetName, String methodName, String[] keys, Object[] arguments) {

        StringBuilder sb = new StringBuilder();
        sb.append("lock.").append(targetName).append(".").append(methodName);

        if (keys != null) {

            String keyStr = Joiner.on(".").skipNulls().join(keys);

            String[] parameters = ReflectParamNames.getNames(targetName, methodName);
            ExpressionParser parser = new SpelExpressionParser();
            Expression expression = parser.parseExpression(keyStr);
            EvaluationContext context = new StandardEvaluationContext();
            int length = parameters.length;
            if (length > 0) {
                for (int i = 0; i < length; i++) {
                    context.setVariable(parameters[i], arguments[i]);
                }
            }
            String keysValue = expression.getValue(context, String.class);
            sb.append("#").append(keysValue);
        }
        return sb.toString();
    }
}