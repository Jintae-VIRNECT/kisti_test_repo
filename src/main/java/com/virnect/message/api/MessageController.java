package com.virnect.message.api;

import com.virnect.message.application.MessageService;
import com.virnect.message.dto.request.MailSendRequest;
import com.virnect.message.exception.MessageException;
import com.virnect.message.global.common.ApiResponse;
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
            value = "메일 전송"
    )
    @PostMapping("/mail")
    public ResponseEntity<ApiResponse> sendMail(@RequestBody @Valid MailSendRequest mailSendRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new MessageException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse apiResponse = messageService.sendMail(mailSendRequest);
        return ResponseEntity.ok(apiResponse);
    }
}
