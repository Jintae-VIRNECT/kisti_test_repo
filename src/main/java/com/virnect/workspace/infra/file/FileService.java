package com.virnect.workspace.infra.file;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

/**
 * Project: service-server
 * DATE: 2019-10-28
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: File Upload Interface for LocalFileUpload and AWS FileUpload
 */
public interface FileService {
    /**
     * 파일 업로드 처리
     * @param file -  파일 업로드 요청
     * @param workspaceUUID
     * @return - 업로드된 파일 url
     */
    String upload(MultipartFile file, String workspaceUUID) throws IOException;

    /**
     * 업로드 된 파일 삭제 요청
     * @param url - 업로드된 파일 url
     */
    void delete(final String url);

    /**
     * 업로드 된 파일 url 조회
     * @param fileName
     * @return
     */
    String getDefaultFileUrl(String fileName);
}
