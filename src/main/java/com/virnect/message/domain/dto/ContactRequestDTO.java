package com.virnect.message.domain.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * Project: PF-Message
 * DATE: 2020-02-12
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class ContactRequestDTO {
    //문의하기 -> message서비스 -> 버넥트 관계자
    private List<String> category;
    private String name;//회사명 or 이름
    private String phone;//연락처

    @NotBlank
    private String sender;//Email

    @NotBlank
    private List<String> receiver; //버넥트

    @NotBlank
    private String subject;//제목

    private Map<String, Object> context; //이름, Email, 연락처, 제목, 내용

    @NotBlank
    private String template; //none 이면 (필수값 : none)
}
