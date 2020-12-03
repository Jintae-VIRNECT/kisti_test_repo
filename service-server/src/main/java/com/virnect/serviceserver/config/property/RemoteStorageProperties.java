package com.virnect.serviceserver.config.property;

import com.virnect.serviceserver.ServiceServerApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConfigurationProperties(prefix = "minio", ignoreInvalidFields = true)
public class RemoteStorageProperties extends PropertyService {

    //Local File Config properties
    private String bucketName;
    private String profileBucketName;
    private String fileBucketName;
    private String recordBucketName;
    private String accessKey;
    private String secretKey;
    private String serverUrl;
    private boolean serviceEnabled;
    private boolean policyEnabled;
    private String policyLocation;

    public String getBucketName() {
        return this.bucketName;
    }

    public String getFileBucketName() {
        return this.fileBucketName;
    }

    public String getRecordBucketName() {
        return this.recordBucketName;
    }

    public String getProfileBucketName() {
        return profileBucketName;
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

    public boolean isServiceEnabled() {
        return serviceEnabled;
    }

    public void setServiceEnabled(boolean enabled) {
        this.serviceEnabled = enabled;
    }

    public boolean isPolicyEnabled() {
        return policyEnabled;
    }

    public String getPolicyLocation() {
        return policyLocation;
    }



    public void checkStorageProperties() {
        this.policyEnabled = asBoolean("storage.policy.enabled");
        this.policyLocation = getValue("storage.policy.location");

        this.serviceEnabled = asBoolean("storage.enabled");
        this.bucketName = getValue("storage.bucket.name");
        this.fileBucketName = getValue("storage.bucket.file");
        this.profileBucketName = getValue("storage.bucket.profile");
        this.recordBucketName = getValue("storage.bucket.record");
        this.accessKey = getValue("storage.access-key");
        this.secretKey = getValue("storage.secret-key");
        this.serverUrl = getValue("storage.serverUrl");

        /*log.info("checkFileServiceProperties {}", bucketName);
        log.info("checkFileServiceProperties {}", fileBucketName);
        log.info("checkFileServiceProperties {}", profileBucketName);
        log.info("checkFileServiceProperties {}", recordBucketName);
        log.info("checkFileServiceProperties {}", accessKey);
        log.info("checkFileServiceProperties {}", secretKey);
        log.info("checkFileServiceProperties {}", serverUrl);
        log.info("checkFileServiceProperties {}", serviceEnabled);

        log.info("checkFileServiceProperties {}", policyEnabled);
        log.info("checkFileServiceProperties {}", policyLocation);*/

        ServiceServerApplication.storageUrl = serverUrl;
    }
}
