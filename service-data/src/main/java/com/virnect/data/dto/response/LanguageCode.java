package com.virnect.data.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.data.dto.constraint.TranslationItem;

@Getter
@Setter
public class LanguageCode {
    @ApiModelProperty(value = "language name")
    private String text;
    @ApiModelProperty(value = "language code")
    private String code;

    public LanguageCode(TranslationItem translationItem) {
        this.text = translationItem.getLanguage();
        this.code = translationItem.getLanguageCode();
    }

    public LanguageCode(String text, String code) {
        this.text = text;
        this.code = code;
    }


    @Override
    public String toString() {
        return "LanguageCode{" +
                "text='" + text + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
