package com.virnect.workspace.global.config;

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

/**
 * Project: PF-Workspace
 * DATE: 2020-01-30
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Profile("!production")
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
    @Bean
    public Docket docket() {
        Contact contact = new Contact("이주경", "https://virnect.com", "ljk@virnect.com");

        ApiInfo apiInfo = new ApiInfoBuilder()
                //.contact(contact)
                //.description("워크스페이스 API")
                .version("v0.0.1")
                .title("VIRNECT Platform Workspace Service API Document.")
                //.license("VIRNECT INC All rights reserved.")
                .build();

        ArrayList<ResponseMessage> responseMessages = new ArrayList<>();
        responseMessages.add(new ResponseMessageBuilder().code(500).message("서버 에러").build());
        responseMessages.add(new ResponseMessageBuilder().code(404).message("잘못된 요청").build());

        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, responseMessages)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.virnect.workspace.api"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo);

    }
}
