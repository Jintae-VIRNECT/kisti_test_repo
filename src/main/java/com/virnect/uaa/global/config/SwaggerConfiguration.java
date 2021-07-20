package com.virnect.uaa.global.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @project: PF-Auth
 * @author: jeonghyeon.chang (johnmark)
 * @email: practice1356@gmail.com
 * @since: 2020.03.09
 * @description: Swagger REST Document Configuration ClassName	Description
 */
@Profile({"!staging", "!production"})
@Configuration
@RequiredArgsConstructor
@EnableSwagger2
public class SwaggerConfiguration {
	private final ObjectMapper objectMapper;

	@Bean
	public Docket authApiDocument() throws JsonProcessingException {
		List<ResponseMessage> responseMessages = new ArrayList<>();
		// for (ErrorCode errorCode : ErrorCode.values()) {
		// 	responseMessages.add(new ResponseMessageBuilder().code(errorCode.getCode())
		// 		.message(objectMapper.writeValueAsString(new ErrorResponseMessage(errorCode)))
		// 		.build());
		// }
		// responseMessages.add(new ResponseMessageBuilder().code(200).message("success").build());

		ApiInfo apiInfo = new ApiInfoBuilder()
			.contact(new Contact("장정현", "https://virnect.com", "sky456139@vinrect.com"))
			.description("인증 서버 API 정보 입니다.")
			.version("v0.0.1")
			.title("VIRNECT Authentication Service API Document.")
			.license("VIRNECT INC All rights reserved.")
			.build();
		return new Docket(DocumentationType.SWAGGER_2)
			.useDefaultResponseMessages(false)
			.globalResponseMessage(RequestMethod.GET, responseMessages)
			.globalResponseMessage(RequestMethod.POST, responseMessages)
			.globalResponseMessage(RequestMethod.PUT, responseMessages)
			.globalResponseMessage(RequestMethod.DELETE, responseMessages)
			.select()
			.apis(RequestHandlerSelectors.basePackage("com.virnect.uaa.domain"))
			.paths(PathSelectors.any())
			.build()
			.apiInfo(apiInfo);
	}
}
