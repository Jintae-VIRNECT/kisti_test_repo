package com.virnect.content.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@RequiredArgsConstructor
public class ContentCountResponse {
    @ApiModelProperty(value = "컨텐츠 수", notes = "컨텐츠 수", example = "200")
    private Long countContents;

    @ApiModelProperty(value = "사용자 식별자", notes = "사용자 식별자")
    private String userUUID;
}
