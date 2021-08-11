package com.virnect.serviceserver.servicedashboard.dto.response;

import java.awt.image.BufferedImage;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import com.virnect.data.error.ErrorCode;

@Getter
@Setter
@Builder
public class FileBufferImageResponse {

	private BufferedImage bufferedImage;
	private String contentType;
	private ErrorCode errorCode;

}
