package com.virnect.content;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;

@SpringBootApplication
public class ContentManagementApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        // PathVariable 내부에 '/'가 들어오는 경우를 해결하기 위해 추가
        System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
        SpringApplication.run(ContentManagementApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    // PathVariable 내부에 '/'가 들어오는 경우를 해결하기 위해 추가
    public void configurePathMatch(PathMatchConfigurer configurer) {
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        urlPathHelper.setUrlDecode(false);
        urlPathHelper.setAlwaysUseFullPath(true);
        configurer.setUrlPathHelper(urlPathHelper);
    }
}
