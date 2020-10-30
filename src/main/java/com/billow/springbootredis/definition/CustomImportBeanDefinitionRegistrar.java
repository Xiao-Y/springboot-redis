package com.billow.springbootredis.definition;

import com.billow.springbootredis.annotation.CustomMapper;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import java.util.Map;

/**
 * @author liuyongtao
 * @create 2020-10-30 14:41
 */
public class CustomImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry, false);

        TypeFilter filter = new AnnotationTypeFilter(CustomMapper.class);

        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(ComponentScan.class.getName());
        String[] basePackages = (String[]) annotationAttributes.get("basePackages");

        System.out.println("=========>>>" + basePackages[0]);

        scanner.addIncludeFilter(filter);// 以下两个有顺序
        scanner.scan(basePackages);
//        scanner.scan("com.billow.springbootredis.custom");


    }
}
