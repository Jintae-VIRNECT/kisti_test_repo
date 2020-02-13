package com.virnect.message.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: PF-Message
 * DATE: 2020-02-12
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Profile({"local", "develop"})
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
    @Bean
    public List<ResponseMessage> globalResponseMessage() {
        ArrayList<ResponseMessage> responseMessages = new ArrayList<>();
        responseMessages.add(new ResponseMessageBuilder().code(500).message("서버 에러").build());
        responseMessages.add(new ResponseMessageBuilder().code(404).message("잘못된 요청").build());
        return responseMessages;
    }

    @Bean
    public Docket userApi() {
        // API 문서 관련 정보 입력
        ApiInfo apiInfo = new ApiInfoBuilder()
                .contact(new Contact("이주경", "https://virnect.com", "ljk@vinrect.com"))
                .description("Message 서버 API 정보 입니다.")
                .version("v0.0.1")
                .title("VIRNECT Message Service API Document.")
                .license("VIRNECT INC All rights reserved.")
                .build();

        // API 문서 생성 시 필요한 설정 정보
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, globalResponseMessage())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.virnect.message.api"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo);
    }
}
