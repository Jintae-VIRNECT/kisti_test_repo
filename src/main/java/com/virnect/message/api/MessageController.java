package com.virnect.message.api;

import com.virnect.message.application.MessageService;
import com.virnect.message.dto.request.ContactRequest;
import com.virnect.message.dto.request.WorkspaceInviteMailRequest;
import com.virnect.message.dto.response.ContactResponse;
import com.virnect.message.dto.response.InviteWorkspaceResponse;
import com.virnect.message.exception.BusinessException;
import com.virnect.message.global.common.ApiResponse;
import com.virnect.message.global.common.CustomCollectionValidator;
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
    private final CustomCollectionValidator customCollectionValidator;

    /**
     * Contact 문의 메일 전송
     *
     * @param contactRequestDTO - 메일 요청 정보
     * @param bindingResult
     * @return - 메일 전송 성공 여부(true or false)
     */
    @ApiOperation(
            value = "CONTACT 메일 전송",
            notes = "사용자로부터 받은 문의사항을 버넥트 관계자에게 메일로 전송합니다."
    )
    @PostMapping("/contact")
    public ResponseEntity<ApiResponse<ContactResponse>> sendContactMail(@RequestBody @Valid ContactRequest contactRequestDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BusinessException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ContactResponse> apiResponse = messageService.sendContactMail(contactRequestDTO);
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * 워크스페이스 초대 메일 전송
     *
     * @param inviteWorkspaceRequestDTO - 워크스페이스 초대 요청 정보
     * @param bindingResult
     * @return - 메일 전송 성공 여부(true or false)
     */
    @ApiOperation(
            value = "워크스페이스 초대 메일 전송",
            notes = "워크스페이스에 초대된 사용자에게 메일을 전송합니다."
    )
    @PostMapping("/workspace/invite")
    public ResponseEntity<ApiResponse<InviteWorkspaceResponse>> sendInviteWorkspaceMail(@RequestBody @Valid WorkspaceInviteMailRequest inviteWorkspaceRequestDTO, BindingResult bindingResult) {
        customCollectionValidator.validate(inviteWorkspaceRequestDTO.getInviteInfos(), bindingResult);
        if (bindingResult.hasErrors()) {
            throw new BusinessException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<InviteWorkspaceResponse> apiResponse = messageService.sendInviteWorkspaceMail(inviteWorkspaceRequestDTO);
        return ResponseEntity.ok(apiResponse);
    }

}
