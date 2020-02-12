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
    @NotBlank
    private String sender;

    @NotBlank
    private List<String> receiver;

    @NotBlank
    private String subject;

    private Map<String, Object> context;

    @NotBlank
    private String template;
}
