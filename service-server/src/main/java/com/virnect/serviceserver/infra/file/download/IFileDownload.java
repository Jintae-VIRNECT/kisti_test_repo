package com.virnect.serviceserver.infra.file.download;

import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Deprecated
public interface IFileDownload {
    //ResponseEntity<byte[]> fileDownload(final String fileName) throws IOException, NoSuchAlgorithmException, InvalidKeyException;
    byte[] fileDownload(final String fileName) throws IOException, NoSuchAlgorithmException, InvalidKeyException;

    String filePreSignedUrl(final String objectPathToName, int expiry) throws IOException, NoSuchAlgorithmException, InvalidKeyException;

    void copyFileS3ToLocal(String fileName);
}
