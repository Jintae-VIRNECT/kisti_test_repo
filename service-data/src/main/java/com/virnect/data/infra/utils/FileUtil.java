package com.virnect.data.infra.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

import com.virnect.data.domain.file.FileConvertStatus;
import com.virnect.data.domain.file.FileType;
import com.virnect.data.dto.request.file.FileUploadRequest;

@Slf4j
@Service
public class FileUtil {

	public File convertUploadMultiFileToFile(MultipartFile multipartFile, String parentPath) {
		File folder = new File(parentPath);
		folder.mkdir();
		File file = new File(folder, multipartFile.getOriginalFilename());
		try (OutputStream fos = new FileOutputStream(file);) {
			fos.write(multipartFile.getBytes());
		} catch (IOException e) {
			return null;
		}
		return file;
	}

	public String generateDirPath(String workspaceId, String sessionId) {
		return "workspace" + "/" + workspaceId + "/" + "remote" + "/" + sessionId + "/";
	}

	public com.virnect.data.domain.file.File buildFile(
		FileUploadRequest fileUploadRequest, String objectName, FileConvertStatus fileConvertStatus
	) {
		return com.virnect.data.domain.file.File.builder()
			.workspaceId(fileUploadRequest.getWorkspaceId())
			.sessionId(fileUploadRequest.getSessionId())
			.uuid(fileUploadRequest.getUserId())
			.name(fileUploadRequest.getFile().getOriginalFilename())
			.objectName(objectName)
			.contentType(fileUploadRequest.getFile().getContentType())
			.size(fileUploadRequest.getFile().getSize())
			.fileType(FileType.OBJECT)
			.fileConvertStatus(fileConvertStatus)
			.height(0)
			.width(0)
			.build();
	}

	public com.virnect.data.domain.file.File buildFileOnly3dObject(
		FileUploadRequest fileUploadRequest, String objectName, FileConvertStatus fileConvertStatus, Long size
	) {
		return com.virnect.data.domain.file.File.builder()
			.workspaceId(fileUploadRequest.getWorkspaceId())
			.sessionId(fileUploadRequest.getSessionId())
			.uuid(fileUploadRequest.getUserId())
			.name(fileUploadRequest.getFile().getOriginalFilename())
			.objectName(objectName)
			.contentType(fileUploadRequest.getFile().getContentType())
			.size(size)
			.fileType(FileType.OBJECT)
			.fileConvertStatus(fileConvertStatus)
			.height(0)
			.width(0)
			.build();
	}

}
