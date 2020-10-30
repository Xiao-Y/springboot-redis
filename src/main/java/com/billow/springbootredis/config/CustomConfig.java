package com.billow.springbootredis.config;

import com.billow.springbootredis.definition.CustomImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author liuyongtao
 * @create 2020-10-30 14:48
 */
@Configuration
@Import(CustomImportBeanDefinitionRegistrar.class)
@ComponentScan("com.billow.springbootredis.custom")
public class CustomConfig {
}
