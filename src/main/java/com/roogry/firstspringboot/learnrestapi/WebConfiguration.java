package com.roogry.firstspringboot.learnrestapi;

import com.roogry.firstspringboot.learnrestapi.resolver.UserArgumentResolver;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

  @Autowired
  private UserArgumentResolver userArgumentResolver;

  @Override
  public void addArgumentResolvers(
    List<HandlerMethodArgumentResolver> resolvers
  ) {
    WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    resolvers.add(userArgumentResolver);
  }
}
