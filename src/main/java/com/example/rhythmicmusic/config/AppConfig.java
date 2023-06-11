package com.example.rhythmicmusic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author sgj
 * @create 2023-05-22 19:48
 */
@Configuration
public class AppConfig implements WebMvcConfigurer {

    @Bean
    public BCryptPasswordEncoder getBCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 添加自定义拦截器加入到系统配置中
    // 除了排除的页面，其他页面都需要登录后才能访问
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LoginInterceptor loginInterceptor = new LoginInterceptor();
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                // 排除拦截以下的页面
                .excludePathPatterns("js/**.js")
                .excludePathPatterns("/images/**")
                .excludePathPatterns("/css/**.css")
                .excludePathPatterns("/fronts/**")
                .excludePathPatterns("/player/**")
                .excludePathPatterns("/login.html")
                // 排除登录接口
                .excludePathPatterns("/user/login");
    }
}
