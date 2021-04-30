package com.virnect.content.infra.file.upload;

import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

/**
 * Project: service-server
 * DATE: 2020-01-09
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: File Upload Interface for LocalFileUpload and AWS FileUpload
 */
public interface FileUploadService {
	/**
	 * 파일 업로드 처리
	 *
	 * @param file     - 업로드 요청 파일
	 * @param fileName - 파일 저장명
	 * @return - 업로드된 파일 url
	 * @throws IOException
	 */
	//String upload(MultipartFile file, String fileName) throws IOException;

	/**
	 * 업로드 된 파일 삭제 요청
	 *
	 * @param url - 업로드된 파일 url
	 */
	boolean delete(final String url);

	/**
	 * 파일 읽어들이기
	 *
	 * @param url - 파일이 저장된 경로
	 * @return - 파일 데이터
	 */
	File getFile(final String url);

	/**
	 * base64로 인코딩된 이미지 파일 저장
	 *
	 * @param base64Image
	 * @return
	 */
	String base64ImageUpload(final String base64Image);

	/**
	 * Multipartfile를 file input stream 으로 읽어와 s3에 업로드하는 메서드
	 * @param file - 업로드하고자하는 MultipartFile
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	String uploadByFileInputStream(MultipartFile file, String fileName) throws IOException;
}