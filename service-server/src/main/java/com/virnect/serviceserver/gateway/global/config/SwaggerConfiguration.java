package com.virnect.serviceserver.gateway.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
    private ApiInfo apiInfo(String title, String version) {
        return new ApiInfoBuilder()
                .contact(new Contact("Contact Me", "www.example.com", "foo@example.com"))
                .description("Remote Service API Docs")
                .version(version)
                .title(title)
                .license("VIRNECT INC All rights reserved")
                .build();
    }

    @Bean
    public List<ResponseMessage> globalResponseMessage() {
        ArrayList<ResponseMessage> responseMessages = new ArrayList<>();
        responseMessages.add(new ResponseMessageBuilder().code(500).message("서버 에러").build());
        responseMessages.add(new ResponseMessageBuilder().code(404).message("잘못된 요청").build());
        return responseMessages;
    }

    @Bean
    public Docket remoteServiceApi() {
        String version = "v1.0";
        String groupName = "remote-service";
        String title = "VIRNEC Remote Service API Document.";

        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, globalResponseMessage())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.virnect.serviceserver.gateway.api"))
                //.paths(PathSelectors.any())
                .paths(PathSelectors.ant("/remote/**"))
                .build()
                .groupName(groupName)
                .apiInfo(apiInfo(title, version));

    }

    /*@Bean
    public Docket apiV2() {
        version = "V2";
        title = "victolee API " + version;

        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .groupName(version)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.victolee.swaggerexam.api.v2"))
                .paths(PathSelectors.ant("/v2/api/**"))
                .build()
                .apiInfo(apiInfo(title, version));

    }*/
}
