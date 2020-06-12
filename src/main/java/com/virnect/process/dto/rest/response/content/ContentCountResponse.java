package com.virnect.process.dto.rest.response.content;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class ContentCountResponse {
    @ApiModelProperty(value = "컨텐츠 수", notes = "컨텐츠 수", example = "200")
    private Long countContents;

    @ApiModelProperty(value = "사용자 식별자", notes = "사용자 식별자")
    private String userUUID;
}
