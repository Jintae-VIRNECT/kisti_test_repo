package com.virnect.serviceserver.global.config.property;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@ConfigurationProperties(prefix = "storage", ignoreInvalidFields = true)
public class RemoteStorageProperties {

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

	@Override
	public String toString() {
		return
			'\t'+
			"* STORAGE_ENABLED=" + enabled + "\n\t" +
			"* STORAGE_SERVERURL=" + serverUrl + "\n\t" +
			"* STORAGE_ACCESS_KEY=" + accessKey + "\n\t" +
			"* STORAGE_SECRET_KEY=" + secretKey + "\n\t" +
			"* STORAGE_BUCKET_NAME=" + bucketName + "\n\t" +
			"* STORAGE_BUCKET_PROFILE=" + bucketProfileName + "\n\t" +
			"* STORAGE_BUCKET_FILE=" + bucketFileName + "\n\t" +
			"* STORAGE_BUCKET_RECORD=" + bucketRecordName + "\n\t" +
			"* STORAGE_POLICY_ENABLED=" + policyEnabled + "\n\t" +
			"* STORAGE_POLICY_LOCATION=" + policyLocation + "\n\t" +
			"* STORAGE_POLICY_LIFECYCLE=" + policyLifeCycle + '\n' +
			"\n\t" + "------------------------" + "\n";
	}
}
