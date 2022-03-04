package com.virnect.download.infra.file.donwload;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Project: PF-Admin
 * DATE: 2020-03-16
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Profile({"staging", "production", "local", "develop"})
@Service
@RequiredArgsConstructor
public class S3FileDownloadService implements FileDownloadService {
	private final AmazonS3 amazonS3Client;

}
