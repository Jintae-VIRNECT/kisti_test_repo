package com.virnect.content.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@RequiredArgsConstructor
public class ContentDeleteListResponse {
    @ApiModelProperty(value = "삭제 처리 결과 목록", notes = "컨텐츠 삭제의 결과를 컨텐츠 목록으로 반환", example = "true")
    private final List<ContentDeleteResponse> deleteResponseList;
}
