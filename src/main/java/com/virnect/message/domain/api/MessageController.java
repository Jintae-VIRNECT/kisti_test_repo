package com.virnect.message.domain.api;

import com.virnect.message.domain.application.MailService;
import com.virnect.message.domain.dto.MailRequestDTO;
import com.virnect.message.domain.exception.BusinessException;
import com.virnect.message.global.common.ResponseMessage;
import com.virnect.message.global.error.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Project: base
 * DATE: 2020-01-07
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Sample Basic Controller
 */
@Slf4j
@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageController {
    private final MailService mailService;

    @PostMapping("/mail")
    public ResponseEntity<ResponseMessage> sendMail(@RequestBody @Valid MailRequestDTO mailRequestDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BusinessException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ResponseMessage responseMessage = mailService.sendTemplateMail(mailRequestDTO);
        return ResponseEntity.ok(responseMessage);
    }
}
