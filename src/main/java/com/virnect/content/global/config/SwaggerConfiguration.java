package com.virnect.content.global.config;

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
public class SwaggerConfiguration {

    @Bean
    public List<ResponseMessage> globalResponseMessage() {
        ArrayList<ResponseMessage> responseMessages = new ArrayList<>();
        responseMessages.add(new ResponseMessageBuilder().code(500).message("서버 에러").build());
        responseMessages.add(new ResponseMessageBuilder().code(404).message("잘못된 요청").build());
        return responseMessages;
    }

    @Bean
    public Docket processApi() {
        ApiInfo apiInfo = new ApiInfoBuilder()
                .contact(new Contact("장정현 / 민항기", "https://virnect.com", "sky456139@vinrect.com / hkmin@virnect.com"))
                .description("공정 서버 API 정보 입니다.")
                .version("v0.0.1")
                .title("VIRNECT Process Service API Document.")
                .license("VIRNECT INC All rights reserved.")
                .build();

        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, globalResponseMessage())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.virnect.smic.domain.process.api"))
                .paths(PathSelectors.any())
                .build()
                .groupName("Process Service")
                .apiInfo(apiInfo);
    }

    @Bean
    public Docket contentApi() {
        ApiInfo apiInfo = new ApiInfoBuilder()
                .contact(new Contact("장정현", "https://virnect.com", "sky456139@vinrect.com"))
                .description("콘텐츠 서버 API 정보 입니다.")
                .version("v0.0.1")
                .title("VIRNECT Content Service API Document.")
                .license("VIRNECT INC All rights reserved.")
                .build();

        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, globalResponseMessage())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.virnect.smic.domain.content.api"))
                .paths(PathSelectors.any())
                .build()
                .groupName("Content Service")
                .apiInfo(apiInfo);
    }
}
