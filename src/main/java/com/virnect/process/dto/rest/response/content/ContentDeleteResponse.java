package com.virnect.process.dto.rest.response.content;

import com.virnect.process.domain.YesOrNo;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@ToString
public class ContentDeleteResponse {
    @ApiModelProperty(value = "워크스페이스 식별자", notes = "해당 식별자를 통해 워크스페이스를 구별합니다.", example = "061cc38d-6c45-445b-bf56-4d164fcb5d29")
    private String workspaceUUID;
    @ApiModelProperty(value = "컨텐츠 고유 식별자", notes = "해당 식별자를 통해 컨텐츠를 구별합니다.", position = 1, example = "061cc38d-6c45-445b-bf56-4d164fcb5d29")
    private String contentUUID;
    @ApiModelProperty(value = "컨텐츠 명", notes = "제작한 컨텐츠의 명칭입니다", position = 2, example = "직박구리 파일")
    private String contentName;
    @ApiModelProperty(value = "컨텐츠 공유", notes = "컨텐츠 공유 상태입니다.", position = 3, example = "직박구리 파일")
    private YesOrNo shared;
    @ApiModelProperty(value = "컨텐츠 파일을 올린 사용자의 식별자", notes = "업로드를 한 사용자의 식별자 입니다.", dataType = "string", position = 6, example = "498b1839dc29ed7bb2ee90ad6985c608")
    private String uploaderUUID;
    @ApiModelProperty(value = "컨텐츠 전환여부", notes = "컨텐츠가 작업으로 전환되었는지 여부", position = 10, example = "YES")
    private YesOrNo converted;
    @ApiModelProperty(value = "컨텐츠 생성 일자", notes = "컨텐츠 생성일자(신규 등록) 기간 정보입니다.", position = 12, example = "2020-02-15 16:32:13")
    private LocalDateTime updatedDate;
    @ApiModelProperty(value = "삭제 처리 결과 메시지", notes = "컨텐츠가 삭제결과 메시지", example = "success")
    private String msg;
    @ApiModelProperty(value = "삭제 처리 결과", notes = "컨텐츠가 삭제되었으면 true, 아니면 false 값을 리턴합니다", example = "true")
    private Boolean result;
}
