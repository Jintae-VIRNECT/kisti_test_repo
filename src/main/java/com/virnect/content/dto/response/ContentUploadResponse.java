package com.virnect.content.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ContentUploadResponse {
    @ApiModelProperty(value= "컨텐츠 고유 식별자", notes = "해당 식별자를 통해 컨텐츠를 구별합니다.", example = "061cc38d-6c45-445b-bf56-4d164fcb5d29")
    private String uuid;
    @ApiModelProperty(value= "컨텐츠 명", notes = "제작한 컨텐츠의 명칭입니다", position = 1, example = "직박구리 파일")
    private String name;
    @ApiModelProperty(value= "컨텐츠 저장 경로", notes = "컨텐츠가 저장된 경로(url)", position = 2, example = "http://localhost:8083/contents/upload/1.Ares")
    private String path;
    @ApiModelProperty(value= "컨텐츠 파일 크기", notes = "컨텐츠 파일 크기로 MB 입니다.", dataType = "int", position = 3, example = "10")
    private int size;
    @ApiModelProperty(value= "컨텐츠 생성 일자", notes = "컨텐츠 생성일자(신규 등록) 기간 정보입니다.", position = 4, example = "2020-02-15T16:32:13.305")
    private LocalDateTime createdDate;
    @ApiModelProperty(value= "컨텐츠 수정 일자", notes = "컨텐츠 수정 일자(수정 등록) 기간 정보입니다.", position = 5, example = "2020-02-15T16:32:13.305")
    private LocalDateTime updatedDate;
}
