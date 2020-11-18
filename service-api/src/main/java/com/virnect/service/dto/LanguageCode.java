package com.virnect.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LanguageCode {
    @ApiModelProperty(value = "language name")
    private String text;
    @ApiModelProperty(value = "language code")
    private String code;

    @Override
    public String toString() {
        return "LanguageCode{" +
                "text='" + text + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
