package com.virnect.serviceserver.gateway.global.config;

import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.virnect.serviceserver.gateway.global.error.ErrorCode;
import com.virnect.serviceserver.gateway.global.error.ErrorResponseMessage;
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

@Profile({"develop", "local"})
@Configuration
@EnableSwagger2
@RequiredArgsConstructor
public class SwaggerConfiguration {
    private final TypeResolver typeResolver;
    private final ObjectMapper objectMapper;

    private ApiInfo apiInfo(String title, String version) {
        return new ApiInfoBuilder()
                .contact(new Contact("김경", "https://virnect.com", "hoon@virnect.com"))
                .description("Remote Service API Docs")
                .version(version)
                .title(title)
                .license("VIRNECT INC All rights reserved")
                .build();
    }

    @Bean
    public List<ResponseMessage> globalResponseMessage() {
        ArrayList<ResponseMessage> responseMessages = new ArrayList<>();
        for (ErrorCode errorCode : ErrorCode.values()) {
            try {
                responseMessages.add(new ResponseMessageBuilder().code(errorCode.getCode()).message(objectMapper.writeValueAsString(new ErrorResponseMessage(errorCode))).build());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        responseMessages.add(new ResponseMessageBuilder().code(200).message("success").build());
        /*responseMessages.add(new ResponseMessageBuilder().code(200).message("success").build());
        responseMessages.add(new ResponseMessageBuilder().code(500).message("서버 에러").build());
        responseMessages.add(new ResponseMessageBuilder().code(404).message("잘못된 요청").build());*/
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
                .globalResponseMessage(RequestMethod.POST, globalResponseMessage())
                .globalResponseMessage(RequestMethod.PUT, globalResponseMessage())
                .globalResponseMessage(RequestMethod.DELETE, globalResponseMessage())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.virnect.serviceserver.gateway.api"))
                .paths(PathSelectors.any())
                //.paths(PathSelectors.ant("/remote/**"))
                .build()
                .additionalModels(typeResolver.resolve(ErrorCode.class))
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
