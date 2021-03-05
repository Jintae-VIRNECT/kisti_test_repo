package com.virnect.serviceserver.global.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
//@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "storage", ignoreInvalidFields = true)
//public class RemoteStorageProperties extends PropertyService {
public class RemoteStorageProperties {

	//Local File Config properties
	private boolean enabled;
	private String serverUrl;
	private String accessKey;
	private String secretKey;
	private String bucketName;
	private String bucketProfileName;
	private String bucketFileName;
	private String bucketRecordName;
	private boolean policyEnabled;
	private String policyLocation;
	private int policyLifeCycle;

	/*public String getBucketName() {
		return this.bucketName;
	}

	public String getBucketFileName() {
		return this.bucketFileName;
	}

	public String getBucketRecordName() {
		return this.bucketRecordName;
	}

	public String getBucketProfileName() {
		return bucketProfileName;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isPolicyEnabled() {
		return policyEnabled;
	}

	public int getPolicyLifeCycle() {
		return policyLifeCycle;
	}

	public String getPolicyLocation() {
		return policyLocation;
	}

	public void checkStorageProperties() {
		this.policyEnabled = asBoolean("storage.policy.enabled");
		this.policyLifeCycle = asNonNegativeInteger("storage.policy.lifecycle");
		this.policyLocation = getValue("storage.policy.location");

		this.enabled = asBoolean("storage.enabled");
		this.bucketName = getValue("storage.bucket.name");
		this.bucketFileName = getValue("storage.bucket.file");
		this.bucketProfileName = getValue("storage.bucket.profile");
		this.bucketRecordName = getValue("storage.bucket.record");
		this.accessKey = getValue("storage.access-key");
		this.secretKey = getValue("storage.secret-key");
		this.serverUrl = getValue("storage.serverUrl");

        *//*log.info("checkFileServiceProperties {}", bucketName);
        log.info("checkFileServiceProperties {}", fileBucketName);
        log.info("checkFileServiceProperties {}", profileBucketName);
        log.info("checkFileServiceProperties {}", recordBucketName);
        log.info("checkFileServiceProperties {}", accessKey);
        log.info("checkFileServiceProperties {}", secretKey);
        log.info("checkFileServiceProperties {}", serverUrl);
        log.info("checkFileServiceProperties {}", serviceEnabled);

        log.info("checkFileServiceProperties {}", policyEnabled);
        log.info("checkFileServiceProperties {}", policyLocation);*//*

		ServiceServerApplication.storageUrl = serverUrl;
	}*/
}
