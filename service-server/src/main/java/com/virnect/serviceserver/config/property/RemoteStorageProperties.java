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
    private String fileBucketName;
    private String profileBucketName;
    private String accessKey;
    private String secretKey;
    private String serverUrl;
    private String rootDirPath;
    private boolean serviceEnabled;

    public String getFileBucketName() {
        return this.fileBucketName;
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

    public String getRootDirPath() {
        return rootDirPath;
    }

    public boolean isServiceEnabled() {
        return serviceEnabled;
    }

    public void setServiceEnabled(boolean enabled) {
        this.serviceEnabled = enabled;
    }



    public void checkStorageProperties() {
        this.fileBucketName = getValue("minio.bucket-file-name");
        this.profileBucketName = getValue("minio.bucket-profile-name");
        this.accessKey = getValue("minio.access-key");
        this.secretKey = getValue("minio.secret-key");
        this.rootDirPath = getValue("minio.dir");
        this.serverUrl = getValue("minio.serverUrl");
        this.serviceEnabled = asBoolean("minio.enabled");

        log.info("checkFileServiceProperties {}", fileBucketName);
        log.info("checkFileServiceProperties {}", profileBucketName);
        log.info("checkFileServiceProperties {}", accessKey);
        log.info("checkFileServiceProperties {}", secretKey);
        log.info("checkFileServiceProperties {}", rootDirPath);
        log.info("checkFileServiceProperties {}", serverUrl);
        log.info("checkFileServiceProperties {}", serviceEnabled);

        ServiceServerApplication.storageUrl = serverUrl;
    }
}
