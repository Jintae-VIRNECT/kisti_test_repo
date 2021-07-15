package com.virnect.message.api;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.message.application.mail.MailService;
import com.virnect.message.dto.request.AttachmentMailRequest;
import com.virnect.message.dto.request.MailSendRequest;
import com.virnect.message.exception.MessageException;
import com.virnect.message.global.common.ApiResponse;
import com.virnect.message.global.error.ErrorCode;

/**
 * Project: PF-Message
 * DATE: 2021-05-13
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MailController {
	private final MailService mailService;
	@ApiOperation(
		value = "메일 전송"
	)
	@PostMapping("/mail")
	public ResponseEntity<ApiResponse<Boolean>> sendMail(@RequestBody @Valid MailSendRequest mailSendRequest, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new MessageException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		Boolean responseMessage = mailService.sendMail(mailSendRequest);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

	@ApiOperation(
		value = "첨부파일 메일 전송"
	)
	@PostMapping(value = "/mail/attachment")
	public ResponseEntity<ApiResponse<Boolean>> sendAttachmentMail(@RequestBody @Valid AttachmentMailRequest mailSendRequest, BindingResult bindingResult) throws
		IOException,
		MessagingException {
		if (bindingResult.hasErrors()) {
			throw new MessageException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		Boolean responseMessage = mailService.sendAttachmentMail(mailSendRequest);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}
}
