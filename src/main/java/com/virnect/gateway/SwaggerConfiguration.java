package com.virnect.gateway;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.json.JacksonModuleRegistrar;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Gateway
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.09
 */
@Slf4j
@Profile({"local", "develop"})
@Configuration
@RequiredArgsConstructor
public class SwaggerConfiguration {
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
                .filter(route -> route.getId().contains("api"))
                .map(route -> createResource(route.getId(), getRouteLocation(route), "2.0"))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private String getRouteLocation(RouteDefinition route) {
        log.info("ROUTE LOCATION:: [{}]", Optional.ofNullable(route.getPredicates().get(0).getArgs().values().toArray()[0])
                .map(String::valueOf)
                .map(s -> s.replace("*", ""))
                .orElse(null));
        return Optional.ofNullable(route.getPredicates().get(0).getArgs().values().toArray()[0])
                .map(String::valueOf)
                .map(s -> s.replace("*", ""))
                .orElse(null);
    }

    private SwaggerResource createResource(String name, String location, String version) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location + "api-docs");
        swaggerResource.setSwaggerVersion(version);

        log.info("[{}}] - [{}] - [{}]", swaggerResource.getName(), swaggerResource.getUrl(), swaggerResource.getSwaggerVersion());
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

        List<ResponseMessage> responseMessages = Arrays.asList(new ResponseMessageBuilder().code(400).message("요청에러").build());

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
                .pathMapping("/api")
                .apiInfo(apiInfo);
    }
}
