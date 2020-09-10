package com.virnect.data.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadData {
    private String bucketName;
    private String key;
    private String url;

    public UploadData(String bucketName, String key, String url) {
        this.bucketName = bucketName;
        this.key = key;
        this.url = url;
    }
}
