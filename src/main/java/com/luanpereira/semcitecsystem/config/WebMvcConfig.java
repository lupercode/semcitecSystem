package com.luanpereira.semcitecsystem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {


    private static String ROOT_DIRECTORY;
    private static String OFFICIAL_DOC_DIRECTORY;
    private static String USER_PROFILE_IMG_DIRECTORY;

    public WebMvcConfig(
            @Value("${rootDirectory}") String rootDirectory,
            @Value("${officialDocDirectory}") String officialDocDirectory,
            @Value("${userProfileImgDirectory}") String userProfileImgDirectory
            ) {
        ROOT_DIRECTORY = rootDirectory;
        OFFICIAL_DOC_DIRECTORY = officialDocDirectory;
        USER_PROFILE_IMG_DIRECTORY = userProfileImgDirectory;
    }

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Adicione a localização do diretório ao resolver de recursos
        registry.addResourceHandler("/rootDirectory/**")
                .addResourceLocations("file:" + ROOT_DIRECTORY);

        registry.addResourceHandler("/officialDocDirectory/**")
                .addResourceLocations("file:" + OFFICIAL_DOC_DIRECTORY);

        registry.addResourceHandler("/userProfileImgDirectory/**")
                .addResourceLocations("file:" + USER_PROFILE_IMG_DIRECTORY);
    }
}
