/*
 * (C) Copyright 2017-2020 OpenVidu (https://openvidu.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.virnect.serviceserver.infra.resources;

import com.virnect.serviceserver.global.config.RemoteServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This class changes the path where static files are served from / to
 * /NEW_FRONTEND_PATH. Entrypoint file index.html must have tag
 * <base href="/NEW_FRONTEND_PATH/">
 * 
 * By default in OpenVidu CE this path is /dashbaord and in OpenVidu PRO is
 * /inspector
 *
 * @author Pablo Fuente (pablofuenteperez@gmail.com)
 */
@Configuration
public class FrontendResourceHandler implements WebMvcConfigurer {

	@Autowired
	RemoteServiceConfig remoteServiceConfig;


	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("*")
				.allowedMethods("*");

	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/" + remoteServiceConfig.getRemoteServiceFrontendDefaultPath())
				.setViewName("redirect:/" + remoteServiceConfig.getRemoteServiceFrontendDefaultPath() + "/");
		registry.addViewController("/" + remoteServiceConfig.getRemoteServiceFrontendDefaultPath() + "/")
				.setViewName("forward:/" + remoteServiceConfig.getRemoteServiceFrontendDefaultPath() + "/index.html");
		//super.addViewControllers(registry);
	}

}
