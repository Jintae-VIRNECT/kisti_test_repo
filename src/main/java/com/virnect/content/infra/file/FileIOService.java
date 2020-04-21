package com.virnect.content.infra.file;

import java.io.File;
import java.io.IOException;

/**
 * @author hangkee.min (henry)
 * @project PF-ContentManagement
 * @email hkmin@virnect.com
 * @description
 * @since 2020.04.14
 */
public interface FileIOService {

    /**
     * sourceFile 파일을 destinationUrl 파일명으로 복제
     *
     * @param sourceUrl      - 원본파일의 경로 (http:// 를 포함전 전체 경로)
     * @param destinationUrl - 복제 후 파일 경로(서버 경로 제외한 디렉토리 경로)
     * @return - 복제 성공 여부
     */
    boolean copyFileWithUrl(final String sourceUrl, final String destinationUrl) throws IOException;

    /**
     * sourceFile 파일을 destinationUrl 파일명으로 복제
     *
     * @param sourceFile     - 원본파일
     * @param destinationUrl - 복제 후 파일 경로(서버 경로 제외한 디렉토리 경로)
     * @return - 복제 성공 여부
     */
    boolean copyFileWithFile(final File sourceFile, final String destinationUrl) throws IOException;

    /**
     * sourceUrl 파일을 destinationUrl 파일명으로 이름 변경
     *
     * @param sourceUrl      - 원본파일의 경로
     * @param destinationUrl - 변경할 파일의 경로
     * @return
     */
    boolean rename(final String sourceUrl, final String destinationUrl) throws IOException;
}
