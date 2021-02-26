package com.virnect.process.infra.file;

/**
 * Project: PF-ProcessManagement
 * DATE: 2021-02-24
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface FileDownloadService {
	byte[] fileDownloadByFileName(String fileName);
}
