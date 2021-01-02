package com.example.demodock.config;

import org.springframework.boot.web.servlet.view.MustacheViewResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MustacheConfiguration implements WebMvcConfigurer {

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
      
      MustacheViewResolver resolver = new MustacheViewResolver();
      resolver.setOrder(1);
      resolver.setCharset("UTF-8");
      resolver.setContentType("text/html;charset=UTF-8");
      resolver.setPrefix("classpath:/templates");
      resolver.setSuffix(".html");
      resolver.setCache(false); // 배포시에는 true
      
      registry.viewResolver(resolver);
      WebMvcConfigurer.super.configureViewResolvers(registry);
  }
}
