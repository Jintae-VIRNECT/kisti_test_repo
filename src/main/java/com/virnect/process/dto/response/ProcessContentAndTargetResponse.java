package com.virnect.process.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProcessContentAndTargetResponse {
    @ApiModelProperty(value = "컨텐츠 타겟정보", notes = "컨텐츠의 타겟에 대한 정보들")
    private List<ProcessTargetResponse> targetList;
    @ApiModelProperty(value = "컨텐츠에 할당할 고유 식별자", notes = "해당 식별자를 통해 컨텐츠가 구별됨.", position = 1, example = "061cc38d-6c45-445b-bf56-4d164fcb5d29")
    private String contentUUID;

    @Builder
    public ProcessContentAndTargetResponse(List<ProcessTargetResponse> targetList, String contentUUID) {
        this.targetList = targetList;
        this.contentUUID = contentUUID;
    }
}
