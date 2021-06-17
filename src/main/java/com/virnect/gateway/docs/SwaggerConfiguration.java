package com.virnect.gateway.docs;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMethod;

import io.swagger.models.auth.In;
import lombok.RequiredArgsConstructor;
import reactor.util.Logger;
import reactor.util.Loggers;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.json.JacksonModuleRegistrar;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Gateway
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.09
 */
@Profile({"!staging", "!production"})
@Configuration
@RequiredArgsConstructor
public class SwaggerConfiguration {
	private static final Logger looger = Loggers.getLogger("com.virnect.gateway.docs.SwaggerConfiguration");
	private final GatewayProperties gatewayProperties;

	@Bean
	public JsonSerializer jsonSerializer(List<JacksonModuleRegistrar> moduleRegistrars) {
		return new JsonSerializer(moduleRegistrars);
	}

	@Primary
	@Bean
	@Lazy
	public SwaggerResourcesProvider swaggerResourcesProvider() {
		return () -> gatewayProperties.getRoutes().stream()
			.filter(route -> !route.getId().contains("websocket"))
			.filter(route -> !route.getId().contains("payletter"))
			.map(route -> createResource(route.getId(), getRouteLocation(route), "2.0"))
			.collect(Collectors.toList());
	}

	private String getRouteLocation(RouteDefinition route) {
		return Optional.ofNullable(route.getPredicates().get(0).getArgs().values().toArray()[0])
			.map(String::valueOf)
			.map(s -> s.replace("*", ""))
			.orElse(null);
	}

	private SwaggerResource createResource(String name, String location, String version) {
		SwaggerResource swaggerResource = new SwaggerResource();
		swaggerResource.setName(name);
		swaggerResource.setLocation(location + "v2/api-docs");
		swaggerResource.setSwaggerVersion(version);
		looger.info(
			"[{}}] - [{}] - [{}]", swaggerResource.getName(), swaggerResource.getUrl(),
			swaggerResource.getSwaggerVersion()
		);
		return swaggerResource;
	}

	@Bean
	public Docket docket() {
		Contact contact = new Contact("장정현", "https://virnect.com", "sky456139@vinrect.com");

		ApiInfo apiInfo = new ApiInfoBuilder()
			.contact(contact)
			.description("Platform 서버 API 정보 입니다.")
			.version("v0.0.1")
			.title("VIRNECT Platform All Service API Document.")
			.license("VIRNECT INC All rights reserved.")
			.build();

		List<ResponseMessage> responseMessages = Collections.singletonList(
			new ResponseMessageBuilder().code(400).message("요청에러").build());

		return new Docket(DocumentationType.SWAGGER_2)
			.useDefaultResponseMessages(true)
			.globalResponseMessage(RequestMethod.GET, responseMessages)
			.globalResponseMessage(RequestMethod.POST, responseMessages)
			.globalResponseMessage(RequestMethod.PUT, responseMessages)
			.globalResponseMessage(RequestMethod.DELETE, responseMessages)
			.select()
			.apis(RequestHandlerSelectors.any())
			.paths(PathSelectors.any())
			.build()
			.apiInfo(apiInfo)
			.securityContexts(Collections.singletonList(securityContext()))
			.securitySchemes(Collections.singletonList(apiKey()));
	}


	private ApiKey apiKey() {
		return new ApiKey("Bearer", HttpHeaders.AUTHORIZATION, In.HEADER.name());
	}

	private SecurityContext securityContext() {
		return SecurityContext.builder()
			.securityReferences(defaultAuth())
			.forPaths(PathSelectors.any())
			.build();
	}

	List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Collections.singletonList(new SecurityReference("JWT", authorizationScopes));
	}
}
