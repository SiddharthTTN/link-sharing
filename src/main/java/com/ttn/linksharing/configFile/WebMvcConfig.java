package com.ttn.linksharing.configFile;

import com.ttn.linksharing.interceptor.CheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> excluded = new ArrayList<>();
        excluded.add("/home");
        excluded.add("/reset");
        excluded.add("/register");
        excluded.add("/checkerror");
        excluded.add("/login");
        registry.addInterceptor(new CheckInterceptor())
        .excludePathPatterns(excluded);
    }
}