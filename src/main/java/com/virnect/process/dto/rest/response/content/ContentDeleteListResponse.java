package com.virnect.process.dto.rest.response.content;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;


@Getter
@Setter
@ToString
public class ContentDeleteListResponse {
    @ApiModelProperty(value = "삭제 처리 결과 목록", notes = "컨텐츠 삭제의 결과를 컨텐츠 목록으로 반환", example = "true")
    private List<ContentDeleteResponse> deleteResponseList;
}
