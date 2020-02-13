package com.virnect.message.api;

import com.virnect.message.application.MailService;
import com.virnect.message.application.MessageService;
import com.virnect.message.dto.ContactRequestDTO;
import com.virnect.message.exception.BusinessException;
import com.virnect.message.global.common.ResponseMessage;
import com.virnect.message.global.error.ErrorCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
@Api(produces = MediaType.APPLICATION_JSON_VALUE, value = "MESSAGE API", consumes = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
public class MessageController {

    private final MessageService messageService;


    @ApiOperation(
            value = "contact 메일 전송",
            notes = "사용자로부터 받은 문의사항을 버넥트 관계자에게 메일로 전송합니다.",
            produces = MediaType.APPLICATION_JSON_VALUE,
            httpMethod = "POST"
    )
    @PostMapping("/contact")
    public ResponseEntity<ResponseMessage> sendContactMail(@RequestBody @Valid ContactRequestDTO contactRequestDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BusinessException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ResponseMessage responseMessage = messageService.sendContactMail(contactRequestDTO);
        return ResponseEntity.ok(responseMessage);
    }
}
