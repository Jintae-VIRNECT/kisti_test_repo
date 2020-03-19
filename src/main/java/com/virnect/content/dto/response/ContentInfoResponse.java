package com.virnect.content.dto.response;

import com.virnect.content.domain.ContentStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ContentInfoResponse {
    @ApiModelProperty(value = "컨텐츠 고유 식별자", notes = "해당 식별자를 통해 컨텐츠를 구별합니다.", example = "061cc38d-6c45-445b-bf56-4d164fcb5d29")
    private String contentUUID;
    @ApiModelProperty(value = "컨텐츠 명", notes = "제작한 컨텐츠의 명칭입니다", position = 1, example = "직박구리 파일")
    private String contentName;
    @ApiModelProperty(value = "씬그룹 수", notes = "컨텐츠에 들어있는 씬그룹들의 수 입니다.", position = 2, example = "10")
    private int sceneGroupTotal;
    @ApiModelProperty(value = "컨텐츠 파일 크기", notes = "컨텐츠 파일 크기로 MB 입니다.", dataType = "int", position = 3, example = "10")
    private long contentSize;
    @ApiModelProperty(value = "컨텐츠 파일을 올린 사용자의 식별자", notes = "업로드를 한 사용자의 식별자 입니다.", dataType = "string", position = 4, example = "498b1839dc29ed7bb2ee90ad6985c608")
    private String uploaderUUID;
    @ApiModelProperty(value = "컨텐츠 파일을 올린 사용자의 이름", notes = "업로드를 한 사용자의 이름입니다.", dataType = "string", position = 5, example = "고길동")
    private String uploaderName;
    @ApiModelProperty(value = "컨텐츠 파일을 올린 사용자의 프로필 이미지 경로", notes = "업로드를 한 사용자의 프로필 이미지 경로입니다.", dataType = "string", position = 6, example = "http://192.168.6.3:8081/users/upload/master.png")
    private String uploaderProfile;
    @ApiModelProperty(value = "컨텐츠 저장 경로", notes = "컨텐츠가 저장된 경로(url)", position = 7, example = "http://localhost:8083/contents/upload/1.Ares")
    private String path;
    @ApiModelProperty(value = "컨텐츠 상태( PUBLISH(배포 중), MANAGED(공정 관리 중), WAIT(배포 대기 중)", notes = "컨텐츠가 배포중인지, 대기인지, 공정으로 관리되었는지 등의 상태 정보입니다.", position = 8, example = "WAIT")
    private ContentStatus status;
    @ApiModelProperty(value = "컨텐츠에 사용된 aruco 값", notes = "컨텐츠와 맵핑된 타겟에 대한 값입니다", position = 9, example = "1")
    private long target;
    @ApiModelProperty(value = "컨텐츠 생성 일자", notes = "컨텐츠 생성일자(신규 등록) 기간 정보입니다.", position = 10, example = "2020-02-15 16:32:13")
    private LocalDateTime createdDate;
}
