package com.virnect.content.dto.response;

import com.virnect.content.domain.YesOrNo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ContentPropertiesResponse {
    @ApiModelProperty(value = "워크스페이스 식별자", notes = "해당 식별자를 통해 워크스페이스를 구별합니다.", example = "061cc38d-6c45-445b-bf56-4d164fcb5d29")
    private String workspaceUUID;
    @ApiModelProperty(value = "컨텐츠 고유 식별자", notes = "해당 식별자를 통해 컨텐츠를 구별합니다.", position = 1, example = "061cc38d-6c45-445b-bf56-4d164fcb5d29")
    private String contentUUID;
    @ApiModelProperty(value = "컨텐츠 명", notes = "제작한 컨텐츠의 명칭입니다", position = 2, example = "직박구리 파일")
    private String contentName;
    @ApiModelProperty(value = "컨텐츠 공유", notes = "컨텐츠 공유 상태입니다.", position = 3, example = "직박구리 파일")
    private YesOrNo shared;
    @ApiModelProperty(value = "씬그룹 수", notes = "컨텐츠에 들어있는 씬그룹들의 수 입니다.", position = 4, example = "10")
    private int sceneGroupTotal;
    @ApiModelProperty(value = "컨텐츠 파일 크기", notes = "컨텐츠 파일 크기로 MB 입니다.", dataType = "int", position = 5, example = "10")
    private long contentSize;
    @ApiModelProperty(value = "컨텐츠 파일을 올린 사용자의 식별자", notes = "업로드를 한 사용자의 식별자 입니다.", dataType = "string", position = 6, example = "498b1839dc29ed7bb2ee90ad6985c608")
    private String uploaderUUID;
    @ApiModelProperty(value = "컨텐츠 파일을 올린 사용자의 이름", notes = "업로드를 한 사용자의 이름입니다.", dataType = "string", position = 7, example = "고길동")
    private String uploaderName;
    @ApiModelProperty(value = "컨텐츠 파일을 올린 사용자의 프로필 이미지 경로", notes = "업로드를 한 사용자의 프로필 이미지 경로입니다.", dataType = "string", position = 8, example = "http://192.168.6.3:8081/users/upload/master.png")
    private String uploaderProfile;
    @ApiModelProperty(value = "컨텐츠 저장 경로", notes = "컨텐츠가 저장된 경로(url)", position = 9, example = "http://localhost:8083/contents/upload/1.Ares")
    private String path;
    @ApiModelProperty(value = "컨텐츠 전환여부", notes = "컨텐츠가 작업으로 전환되었는지 여부", position = 10, example = "YES")
    private YesOrNo converted;
    @ApiModelProperty(value = "컨텐츠 생성 일자", notes = "컨텐츠 생성일자(신규 등록) 기간 정보입니다.", position = 12, example = "2020-02-15 16:32:13")
    private LocalDateTime createdDate;
    @ApiModelProperty(value = "컨텐츠 속성 메타데이터", notes = "컨텐츠 속성 메타데이터", position = 13, example = "{\"content\":[{\"PropertyInfo\":{\"ComponentName\":\"SceneGroup\",\"ComponentType\":\"SceneGroup\",\"identifier\":\"8b7860ef-7617-4c92-a272-50f4e60e127e\",\"sceneGroupDetail\":\"\",\"sceneGroupTitle\":\"\"},\"child\":[{\"PropertyInfo\":{\"ComponentName\":\"Scene\",\"ComponentType\":\"Scene\",\"identifier\":\"2f6b453a-f5b0-406e-8f45-04f222279f25\",\"sceneDetail\":\"\",\"sceneTitle\":\"\"},\"Transform\":{},\"child\":[{\"PropertyInfo\":{\"ComponentName\":\"Text\",\"ComponentType\":\"Text\",\"alignment\":\"MiddleLeft\",\"backGround\":\"TextBoxBg/0$1|1|1|1$0\",\"color\":\"1|1|1|1\",\"font\":\"NotoSansCJKkr-Bold (UnityEngine.Font)\",\"fontSize\":\"32\",\"identifier\":\"978a9d27-de13-4bfc-8a25-644e3b446c9a\",\"shadow\":\"0\",\"text\":\"텍스트를 입력해주세요\"},\"Transform\":{\"ScreenMode\":\"World\",\"screenPosition\":\"0|0|0\",\"screenRotation\":\"0|0|0\",\"screenScale\":\"1|1|1\",\"worldPosition\":\"0.2940716|0|0\",\"worldRotation\":\"0|0|0\",\"worldScale\":\"1|1|1\"}}]}]}]}")
    private String propertiesMetadata;

    @Builder
    public ContentPropertiesResponse(String workspaceUUID, String contentUUID, String contentName, YesOrNo shared, int sceneGroupTotal, long contentSize, String uploaderUUID, String uploaderName, String uploaderProfile, String path, YesOrNo converted, LocalDateTime createdDate, String propertiesMetadata) {
        this.workspaceUUID = workspaceUUID;
        this.contentUUID = contentUUID;
        this.contentName = contentName;
        this.shared = shared;
        this.sceneGroupTotal = sceneGroupTotal;
        this.contentSize = contentSize;
        this.uploaderUUID = uploaderUUID;
        this.uploaderName = uploaderName;
        this.uploaderProfile = uploaderProfile;
        this.path = path;
        this.converted = converted;
        this.createdDate = createdDate;
        this.propertiesMetadata = propertiesMetadata;
    }
}
