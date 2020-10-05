package com.virnect.gateway.docs;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.models.Swagger;
import lombok.RequiredArgsConstructor;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Gateway
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.10
 */
@Profile({"local", "develop","onpremise"})
@Controller
@RequiredArgsConstructor
public class SwaggerController {
	private final JsonSerializer jsonSerializer;
	private final SwaggerResourcesProvider swaggerResources;

	@GetMapping({"/swagger-resources/configuration/security"})
	@ResponseBody
	public ResponseEntity<SecurityConfiguration> securityConfiguration() {
		return ResponseEntity.ok(SecurityConfigurationBuilder.builder().build());
	}

	@GetMapping({"/swagger-resources/configuration/ui"})
	@ResponseBody
	public ResponseEntity<UiConfiguration> uiConfiguration() {
		return ResponseEntity.ok(UiConfigurationBuilder.builder().build());
	}

	@GetMapping({"/swagger-resources"})
	@ResponseBody
	public ResponseEntity<List<SwaggerResource>> swaggerResources() {
		return ResponseEntity.ok(this.swaggerResources.get());
	}

	@GetMapping(
		value = {"/v2/api-docs"},
		produces = {"application/json", "application/hal+json"}
	)
	@ResponseBody
	public ResponseEntity<Json> getDocumentation() {
		Swagger swagger = new Swagger();
		return ResponseEntity.ok(this.jsonSerializer.toJson(swagger));
	}
}
