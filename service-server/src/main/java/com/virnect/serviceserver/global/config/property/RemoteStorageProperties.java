package com.virnect.serviceserver.global.config.property;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "storage", ignoreInvalidFields = true)
public class RemoteStorageProperties extends PropertyService {

	@NotNull
	private boolean enabled;
	private String serverUrl;
	private String accessKey;
	private String secretKey;
	private String bucketName;
	private String bucketProfileName;
	private String bucketFileName;
	private String bucketRecordName;

	@NotNull
	private boolean policyEnabled;
	private String policyLocation;

	@PositiveOrZero
	private int policyLifeCycle;

	public void setStorageProperties() {
		this.enabled = (boolean)getValue("storage.enabled", enabled);
		this.serverUrl = getValue("storage.serverUrl", serverUrl).toString();
		this.accessKey = getValue("storage.access-key", accessKey).toString();
		this.secretKey = getValue("storage.secret-key", secretKey).toString();
		this.bucketName = getValue("storage.bucket-name", bucketName).toString();
		this.bucketProfileName = getValue("storage.bucket-profile-name", bucketProfileName).toString();
		this.bucketFileName = getValue("storage.bucket-file-name", bucketFileName).toString();
		this.bucketRecordName = getValue("storage.bucket-record-name", bucketRecordName).toString();
		this.policyEnabled = (boolean)getValue("storage.policy-enabled", policyEnabled);
		this.policyLocation = getValue("storage.policy-location", policyLocation).toString();
		this.policyLifeCycle = (int)getValue("storage.policy-lifecycle", policyLifeCycle);
	}

	@Override
	public String toString() {
		return
			'\t'+
			"* STORAGE_ENABLED=" + this.enabled + "\n\t" +
			"* STORAGE_SERVERURL=" + this.serverUrl + "\n\t" +
			"* STORAGE_ACCESS_KEY=" + this.accessKey + "\n\t" +
			"* STORAGE_SECRET_KEY=" + this.secretKey + "\n\t" +
			"* STORAGE_BUCKET_NAME=" + this.bucketName + "\n\t" +
			"* STORAGE_BUCKET_PROFILE=" + this.bucketProfileName + "\n\t" +
			"* STORAGE_BUCKET_FILE=" + this.bucketFileName + "\n\t" +
			"* STORAGE_BUCKET_RECORD=" + this.bucketRecordName + "\n\t" +
			"* STORAGE_POLICY_ENABLED=" + this.policyEnabled + "\n\t" +
			"* STORAGE_POLICY_LOCATION=" + this.policyLocation + "\n\t" +
			"* STORAGE_POLICY_LIFECYCLE=" + this.policyLifeCycle + '\n' +
			"\n\t" + "------------------------" + "\n";
	}
}
