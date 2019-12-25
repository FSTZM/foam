package run.foam.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import run.foam.app.interceptor.TokenInterceptor;

/**
 * 拦截器生效哦
 */
@Configuration
@EnableConfigurationProperties(FilterProperties.class)
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private FilterProperties filterProperties;

    @Value("${foam.jwt.secret}")
    private String secret;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // new 是自己在创建对象，但是拦截器中要使用spring，如果使用spring，就不能自己创建，要用spring来创建
        registry.addInterceptor(new TokenInterceptor(filterProperties,secret)).addPathPatterns("/**");
    }
}