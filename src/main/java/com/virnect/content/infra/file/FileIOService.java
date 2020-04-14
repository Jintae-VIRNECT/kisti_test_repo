package com.virnect.content.infra.file;

/**
 * @author hangkee.min (henry)
 * @project PF-ContentManagement
 * @email hkmin@virnect.com
 * @description
 * @since 2020.04.14
 */
public interface FileIOService {

    /**
     * target 파일을 destination 파일명으로 복제
     *
     * @param targetUrl - 복제할 파일 경로
     * @param destinationaUrl - 복제 후 파일 경로
     * @return - 복제 성공 여부
     */
    boolean copyFile(final String targetUrl, final String destinationaUrl);
}
