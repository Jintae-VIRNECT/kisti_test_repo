package com.virnect.service.dto;

import com.virnect.service.constraint.TranslationItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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


    @Override
    public String toString() {
        return "LanguageCode{" +
                "text='" + text + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
