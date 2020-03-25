package com.virnect.process.dto.response;

import com.virnect.process.domain.Result;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class SmartToolItemResponse {

    @ApiModelProperty(value = "스마트툴 아이템 식별자", notes = "스마트툴 아이템의 식별자", example = "1")
    private long id;

    @ApiModelProperty(value = "체결번호", notes = "체결번호", position = 1, example = "1")
    private int batchCount;

    @ApiModelProperty(value = "체결 작업 토크값", notes = "체결 작업된 토크값", position = 2, example = "12.20")
    private String workingToque;

    @ApiModelProperty(value = "체결 결과", notes = "스마트툴에서 판정한 체결 결과", position = 3, example = "success")
    private Result result;

    @Builder
    public SmartToolItemResponse(long id, int batchCount, String workingToque, Result result) {
        this.id = id;
        this.batchCount = batchCount;
        this.workingToque = workingToque;
        this.result = result;
    }
}
