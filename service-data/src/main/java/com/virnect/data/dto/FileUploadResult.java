package com.virnect.data.dto;

import lombok.Getter;
import lombok.Setter;

import com.virnect.data.domain.file.File;
import com.virnect.data.error.ErrorCode;

@Getter
@Setter
public class FileUploadResult {

	private String objectName;
	private File file;
	private ErrorCode errorCode;

	public FileUploadResult(File file, ErrorCode errorCode) {
		this.file = file;
		this.errorCode = errorCode;
	}

	public FileUploadResult(String objectName, File file, ErrorCode errorCode) {
		this.objectName = objectName;
		this.file = file;
		this.errorCode = errorCode;
	}

}
