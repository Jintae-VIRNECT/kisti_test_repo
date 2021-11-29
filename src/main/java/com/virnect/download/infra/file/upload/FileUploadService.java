package com.virnect.download.infra.file.upload;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

/**
 * Project: PF-Download
 * DATE: 2020-08-06
 * AUTHOR: jeonghyeon.chang (johnmark)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
public interface FileUploadService {
	/**
	 * 파일 업로드 처리
	 *
	 * @param file -  파일 업로드 요청
	 * @return - 업로드된 파일 url
	 */
	String upload(MultipartFile file);

	/**
	 * 업로드 된 파일 삭제 요청
	 *
	 * @param url - 업로드된 파일 url
	 */
	boolean delete(final String url);
}