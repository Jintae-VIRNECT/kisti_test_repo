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

package com.virnect.serviceserver.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.virnect.serviceserver.config.property.RemoteServiceProperties;
import com.virnect.serviceserver.config.property.RemoteStorageProperties;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.virnect.java.client.RemoteServiceRole;
import com.virnect.serviceserver.config.Dotenv.DotenvFormatException;

//@Component
//@EnableConfigurationProperties(RemoteServiceProperties.class)
@Configuration
@EnableConfigurationProperties({
		RemoteServiceProperties.class,
		RemoteStorageProperties.class
})
//@ComponentScan(value = "com.virnect.serviceserver")
public class RemoteServiceConfig {

	public static class Error {
		private String property;
		private String value;
		private String message;

		public Error(String property, String value, String message) {
			super();
			this.property = property;
			this.value = value;
			this.message = message;
		}

		public String getProperty() {
			return property;
		}

		public String getValue() {
			return value;
		}

		public String getMessage() {
			return message;
		}

		@Override
		public String toString() {
			return "Error [property=" + property + ", value=" + value + ", message=" + message + "]";
		}
	}

	protected static final Logger log = LoggerFactory.getLogger(RemoteServiceConfig.class);

	private static final boolean SHOW_PROPERTIES_AS_ENV_VARS = true;

	private List<Error> configErrors = new ArrayList<>();

	private List<String> userConfigProps;

	@Autowired
	public RemoteServiceProperties remoteServiceProperties;

	@Autowired
	public RemoteStorageProperties remoteStorageProperties;

	/*public final RemoteServiceProperties remoteServiceProperties;

	@Autowired
	public RemoteServiceConfig(RemoteServiceProperties remoteServiceProperties) {
		this.remoteServiceProperties = remoteServiceProperties;
	}*/

	/*@Bean
	@ConditionalOnMissingBean
	public RemoteServiceProperties remoteServiceProperties(RemoteServiceProperties remoteServiceProperties) {
		//var serviceProperties = ServiceProperties()
		//return new RemoteServiceProperties();
		return remoteServiceProperties;
	}*/


	//private Map<String, ?> propertiesSource;

	/*@Autowired
	protected Environment env;*/

	/*@Value("#{'${spring.profiles.active:}'.length() > 0 ? '${spring.profiles.active:}'.split(',') : \"default\"}")
	protected String springProfile;


	// Derived properties methods

	public String getSpringProfile() {
		return springProfile;
	}*/

	@Value("#{'${spring.profiles.active:}'.length() > 0 ? '${spring.profiles.active:}'.split(',') : \"default\"}")
	protected String springProfile;

	public String getSpringProfile() {
		return springProfile;
	}

	public String getFinalUrl() {
		//return finalUrl;
		return this.remoteServiceProperties.getFinalUrl();
	}

	public List<String> getKmsUris() {
		// add all kms uris
		List<String> kmsUris = new ArrayList<>();
		kmsUris.addAll(this.remoteServiceProperties.getKmsUrisConference());
		kmsUris.addAll(this.remoteServiceProperties.getKmsUrisStreaming());
		return kmsUris;
	}

	public boolean isCdrEnabled() {
		return this.remoteServiceProperties.isCdrEnabled();
	}

	public boolean isWebhookEnabled() {
		return this.remoteServiceProperties.isWebhookEnabled();
	}

	/*public void setFinalUrl(String finalUrlParam) {
		finalUrl = finalUrlParam.endsWith("/") ? (finalUrlParam) : (finalUrlParam + "/");
	}*/

	public boolean isTurnadminAvailable() {
		//return this.isTurnadminAvailable;
		return this.remoteServiceProperties.isTurnadminAvailable();
	}

	public void setTurnadminAvailable(boolean available) {
		//this.isTurnadminAvailable = available;
		this.remoteServiceProperties.setTurnadminAvailable(available);
	}

	public RemoteServiceRole[] getRolesFromRecordingNotification() {
		RemoteServiceRole[] roles;
		switch (this.remoteServiceProperties.getRemoteServiceRecordingNotification()) {
			case none:
				roles = new RemoteServiceRole[0];
				break;
			case moderator:
				roles = new RemoteServiceRole[]{RemoteServiceRole.MODERATOR};
				break;
			case all:
				roles = new RemoteServiceRole[]{RemoteServiceRole.SUBSCRIBER, RemoteServiceRole.PUBLISHER, RemoteServiceRole.MODERATOR};
				break;
			case publisher_moderator:
			default:
				roles = new RemoteServiceRole[]{RemoteServiceRole.PUBLISHER, RemoteServiceRole.MODERATOR};
		}
		return roles;
	}

	public boolean isRemoteServiceSecret(String secret) {
		return secret.equals(this.remoteServiceProperties.getRemoteServiceSecret());
	}

	public String getCoturnDatabaseString() {
		return "\"ip=" + this.remoteServiceProperties.getCoturnDatabaseIp() +
				" dbname=" + this.remoteServiceProperties.getCoturnDatabaseDbname() +
				" password=" + this.remoteServiceProperties.getCoturnDatabasePassword() +
				" connect_timeout=" + this.remoteServiceProperties.getCoturnDatabaseConnectTimeout() +
				"\"";
	}

	public boolean remoteServiceRecordingCustomLayoutChanged(String path) {
		return !"/opt/remoteService/custom-layout".equals(path);
	}

	public String getRemoteServiceFrontendDefaultPath() {
		return "dashboard";
	}

	// Properties management methods
	/*public RemoteServiceConfig deriveWithAdditionalPropertiesSource(Map<String, ?> propertiesSource) {
		RemoteServiceConfig config = newRemoteServiceConfig();
		config.propertiesSource = propertiesSource;
		config.env = env;
		return config;
	}*/

	/*protected RemoteServiceConfig newRemoteServiceConfig() {
		return new RemoteServiceConfig();
	}*/

	public List<Error> getConfigErrors() {
		//return configErrors;
		return this.remoteServiceProperties.getPropertiesErrors();
	}

	public Map<String, String> getConfigProps() {
		//return configProps;
		return this.remoteServiceProperties.configProps;
	}

	public List<String> getUserProperties() {
		return userConfigProps;
	}

	public String getPropertyName(String propertyName) {
		if (SHOW_PROPERTIES_AS_ENV_VARS) {
			return propertyName.replace('.', '_').replace('-', '_').toUpperCase();
		} else {
			return propertyName;
		}
	}
	public void checkConfiguration(boolean loadDotenv) {
		try {
			if(loadDotenv) {
				this.remoteServiceProperties.setDotenvPath();
				populatePropertySourceFromDotenv();
			}
			this.remoteServiceProperties.checkConfigurationProperties(loadDotenv);
			this.remoteStorageProperties.checkStorageProperties();
		} catch (Exception e) {
			log.error("Exception checking configuration", e);
			//addError(null, "Exception checking configuration." + e.getClass().getName() + ":" + e.getMessage());
			this.remoteServiceProperties.addError(null, "Exception checking configuration." + e.getClass().getName() + ":" + e.getMessage());
		}
		//userConfigProps = new ArrayList<>(configProps.keySet());
		userConfigProps = new ArrayList<>(this.remoteServiceProperties.configProps.keySet());
		userConfigProps.removeAll(getNonUserProperties());
	}

	@PostConstruct
	public void checkConfiguration() {
		this.checkConfiguration(true);
	}

	protected List<String> getNonUserProperties() {
		return Arrays.asList("server.port", "SERVER_PORT", "DOTENV_PATH", "COTURN_IP", "COTURN_REDIS_IP",
				"COTURN_REDIS_DBNAME", "COTURN_REDIS_PASSWORD", "COTURN_REDIS_CONNECT_TIMEOUT");
	}

	protected void populatePropertySourceFromDotenv() {
		File dotenvFile = this.getDotenvFile();
		if (dotenvFile != null) {
			if (dotenvFile.canRead()) {
				Dotenv dotenv = new Dotenv();
				try {
					dotenv.read(dotenvFile.toPath());
					//this.propertiesSource = dotenv.getAll();
					this.remoteServiceProperties.propertiesSource = dotenv.getAll();
					log.info("Configuration properties read from file {}", dotenvFile.getAbsolutePath());
				} catch (IOException | DotenvFormatException e) {
					log.error("Error reading properties from .env file: {}", e.getMessage());
					this.remoteServiceProperties.addError(null, e.getMessage());
					//addError(null, e.getMessage());
				}
			} else {
				//log.error("RemoteService does not have read permissions over .env file at {}", this.getDotenvPath());
				log.error(
						"RemoteService does not have read permissions over .env file at {}",
						this.remoteServiceProperties.getDotenvPath());
			}
		}
	}

	public Path getDotenvFilePathFromDotenvPath(String dotenvPathProperty) {
		if (dotenvPathProperty.endsWith(".env")) {
			// Is file
			return Paths.get(dotenvPathProperty);
		} else if (dotenvPathProperty.endsWith("/")) {
			// Is folder
			return Paths.get(dotenvPathProperty + ".env");
		} else {
			// Is a folder not ending in "/"
			return Paths.get(dotenvPathProperty + "/.env");
		}
	}

	public File getDotenvFile() {
		String envPath = this.remoteServiceProperties.getDotenvPath();
		if (envPath != null && !envPath.isEmpty()) {
			Path path = getDotenvFilePathFromDotenvPath(envPath);
			String normalizePath = FilenameUtils.normalize(path.toAbsolutePath().toString());
			File file = new File(normalizePath);

			if (file.exists()) {
				return file;
			} else {
				log.error(".env file not found at {}", file.getAbsolutePath().toString());
			}

		} else {
			log.warn("DOTENV_PATH configuration property is not defined");
		}
		return null;
		/*if (getDotenvPath() != null && !getDotenvPath().isEmpty()) {

			Path path = getDotenvFilePathFromDotenvPath(getDotenvPath());
			String normalizePath = FilenameUtils.normalize(path.toAbsolutePath().toString());
			File file = new File(normalizePath);

			if (file.exists()) {
				return file;
			} else {
				log.error(".env file not found at {}", file.getAbsolutePath().toString());
			}

		} else {
			log.warn("DOTENV_PATH configuration property is not defined");
		}
		return null;*/
	}

}
