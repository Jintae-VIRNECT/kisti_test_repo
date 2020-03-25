package com.virnect.process.dto.response;

import com.virnect.process.domain.ItemType;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
public class ItemResponse {

    @ApiModelProperty(value = "리포트아이템 식별자", notes = "리포트아이템의 식별자", example = "1")
    private final long id;

    @ApiModelProperty(value = "질문", notes = "항목 질문", position = 1, example = "질문 내용입니까?")
    private final String title;

    @ApiModelProperty(value = "답변 또는 설명", notes = "질문에 대한 답변 또는 설명", position = 2, example = "답변 설명입니다.")
    private final String answer;

    @ApiModelProperty(value = "리포트아이템 종류", notes = "리포트를 구성하는 아이템의 종류 - input_field, toggle, photo", position = 3, example = "INPUT_FIELD")
    private final ItemType type;

    @ApiModelProperty(value = "순번", notes = "항목의 순번", position = 4, example = "1")
    private final int priority;

    @ApiModelProperty(value = "사진파일 경로", notes = "사진파일 경로", position = 5, example = "http://localhost:8083/process/issue/photo/1.jpg")
    private final String photoFilePath;

    @Builder
    public ItemResponse(long id, String title, String answer, ItemType type, int priority, String photoFilePath) {
        this.id = id;
        this.title = title;
        this.answer = answer;
        this.type = type;
        this.priority = priority;
        this.photoFilePath = photoFilePath;
    }
}
