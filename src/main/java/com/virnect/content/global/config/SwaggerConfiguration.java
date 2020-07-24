package com.virnect.content.global.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.virnect.content.global.error.ErrorCode;
import com.virnect.content.global.error.ErrorResponseMessage;
import lombok.RequiredArgsConstructor;
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
 * Project: PF-ContentManagement
 * DATE: 2020-02-03
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Profile({"local", "develop"})
@Configuration
@EnableSwagger2
@RequiredArgsConstructor
public class SwaggerConfiguration {
    private final ObjectMapper objectMapper;

    @Bean
    public Docket contentApi() throws JsonProcessingException {
        ApiInfo apiInfo = new ApiInfoBuilder()
                .contact(new Contact("허지용", "https://virnect.com", "jiyong.heo@vinrect.com"))
                .description("컨텐츠 서버 API 정보 입니다.")
                .version("v0.0.1")
                .title("VIRNECT PRODUCT - Content Service API Document.")
                .license("VIRNECT INC All rights reserved.")
                .build();

        List<ResponseMessage> responseMessages = new ArrayList<>();
        for (ErrorCode errorCode : ErrorCode.values()) {
            responseMessages.add(new ResponseMessageBuilder().code(errorCode.getCode()).message(objectMapper.writeValueAsString(new ErrorResponseMessage(errorCode))).build());
        }
        responseMessages.add(new ResponseMessageBuilder().code(200).message("success").build());

        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, responseMessages)
                .globalResponseMessage(RequestMethod.POST, responseMessages)
                .globalResponseMessage(RequestMethod.PUT, responseMessages)
                .globalResponseMessage(RequestMethod.DELETE, responseMessages)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.virnect.content.api"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo);
    }
}
