package com.virnect.message.application;

import com.virnect.message.dao.MailHistoryRepository;
import com.virnect.message.domain.MailHistory;
import com.virnect.message.domain.MailSender;
import com.virnect.message.dto.request.MailSendRequest;
import com.virnect.message.exception.MessageException;
import com.virnect.message.global.common.ApiResponse;
import com.virnect.message.global.error.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Project: PF-Message
 * DATE: 2020-02-13
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageService {
    private final MailService mailService;
    private final MailHistoryRepository mailHistoryRepository;

    public ApiResponse<Boolean> sendMail(MailSendRequest mailSendRequest) {

        for (String receiver : mailSendRequest.getReceivers()) {
            MailHistory mailHistory = MailHistory.builder()
                    .receiver(receiver)
                    .sender(mailSendRequest.getSender())
                    .contents(mailSendRequest.getHtml())
                    .subject(mailSendRequest.getSubject())
                    .resultCode(HttpStatus.OK.value())
                    .build();
/*
            if (!MailSender.MASTER.getSender().equals(mailSendRequest.getSender())) {
                mailHistory.setResultCode(ErrorCode.ERR_NOT_SUPPORTED_MAIL_SENDER.getCode());

                this.mailHistoryRepository.save(mailHistory);
                throw new MessageException(ErrorCode.ERR_NOT_SUPPORTED_MAIL_SENDER);
            }*/
            mailService.sendMail(receiver, mailSendRequest.getSender(), mailSendRequest.getSubject(), mailSendRequest.getHtml());

            log.info("[메일 전송 완료] - 받는 사람 [" + receiver + "]");

            this.mailHistoryRepository.save(mailHistory);
        }

        return new ApiResponse<>(true);
    }
}

