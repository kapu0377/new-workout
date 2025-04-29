package org.zerock.workoutproject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CustomServletConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //addResourceHandler("URL 경로").addResourceLocations("실제 파일 경로")
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
        registry.addResourceHandler("/fonts/**")
                .addResourceLocations("classpath:/static/fonts/");
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/");
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");

        // template 폴더의 파일 불러오기 설정
//        registry.addResourceHandler("/template/**").addResourceLocations("classpath:/template/");
        // 외부 c드라이브의 download 폴더의 파일 불러오기 설정
//        registry.addResourceHandler("/download/**").addResourceLocations("file:///c:/download/");
    }
}
