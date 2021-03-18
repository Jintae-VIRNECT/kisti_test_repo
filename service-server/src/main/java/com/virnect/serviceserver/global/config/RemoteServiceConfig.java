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

package com.virnect.serviceserver.global.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.BindingResult;

import lombok.RequiredArgsConstructor;

import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.infra.utils.LogMessage;
import com.virnect.serviceserver.global.config.property.RemoteServiceProperties;
import com.virnect.serviceserver.global.config.property.RemoteStorageProperties;
import com.virnect.serviceserver.infra.utils.Dotenv;
import com.virnect.serviceserver.infra.utils.Dotenv.DotenvFormatException;

@Configuration
@EnableConfigurationProperties({
	RemoteServiceProperties.class,
	RemoteStorageProperties.class
})
@ComponentScan(basePackages = {
	"com.virnect.mediaserver.config"
})
@RequiredArgsConstructor
public class RemoteServiceConfig {

	protected static final Logger log = LoggerFactory.getLogger(RemoteServiceConfig.class);
	private static final String TAG = RemoteServiceConfig.class.getSimpleName();

	public final RemoteServiceProperties remoteServiceProperties;
	public final RemoteStorageProperties remoteStorageProperties;

	@PostConstruct
	protected void setMediaServerProperties(){
		this.remoteServiceProperties.setMediaServerProperties();
	}

	@PostConstruct
	public void checkConfiguration() {
		boolean isDotenvEnabled = this.remoteServiceProperties.isDotenv();
		this.checkConfiguration(isDotenvEnabled);
		//this.remoteServiceProperties.mediaServerProperties.setSpringProfile(springProfile);
	}

	@PostConstruct
	public void getPropertyDotenv(){
		Dotenv dotenv = new Dotenv();
		if (remoteServiceProperties.isDotenv()) {
			File dotenvFile = dotenv.getDotenvFile(remoteServiceProperties.getDotenvPath());
			if (dotenvFile != null) {
				if (dotenvFile.canRead()) {
					try {
						dotenv.read(dotenvFile.toPath());
						//this.remoteServiceProperties.propertiesSource = dotenv.getAll();
						log.info("Configuration properties read from file {}", dotenvFile.getAbsolutePath());
					} catch (IOException | DotenvFormatException e) {
						log.error("Error reading properties from .env file: {}", e.getMessage());
						//this.remoteServiceProperties.addError(null, e.getMessage());
					}
				} else {
					log.error(
						"RemoteService does not have read permissions over .env file at {}",
						this.remoteServiceProperties.getDotenvPath()
					);
				}
			}
		} else {
			log.warn("Be sure checking .env file does not use.");
		}
	}

	public List<String> getKmsUris() {
		// add all kms uris
		List<String> kmsUris = new ArrayList<>();
		kmsUris.addAll(this.remoteServiceProperties.getKmsUrisConference());
		kmsUris.addAll(this.remoteServiceProperties.getKmsUrisStreaming());
		return kmsUris;
	}

	public void checkConfiguration(boolean loadDotenv) {
		try {
			this.remoteServiceProperties.checkConfigurationProperties(loadDotenv);
		} catch (Exception e) {
			log.error("Exception checking configuration", e);
			//this.remoteServiceProperties.addError(null, "Exception checking configuration." + e.getClass().getName() + ":" + e.getMessage());
		}
	}

}
