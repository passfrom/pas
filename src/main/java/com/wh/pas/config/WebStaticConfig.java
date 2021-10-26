package com.wh.pas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @Author ktt
 * @Date 2021/7/16 15:53
 **/
/*
 *@ClassName WebStaticConfig
 *@Description TODO
 *@Author ktt
 *@Date 2021/7/16 15:53
 *@Version 1.0
 */
@Configuration
public class WebStaticConfig extends WebMvcConfigurationSupport {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }
}
