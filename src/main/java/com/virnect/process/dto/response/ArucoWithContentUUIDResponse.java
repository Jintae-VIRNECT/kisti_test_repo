package com.virnect.process.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ArucoWithContentUUIDResponse {
    @ApiModelProperty(value = "컨텐츠에 할당할 aruco 값", notes = "컨텐츠가 증강하기 위한 마커의 값(0~999)", example = "0")
    private long aruco;
    @ApiModelProperty(value = "컨텐츠에 할당할 고유 식별자", notes = "해당 식별자를 통해 컨텐츠가 구별됨.", position = 1, example = "061cc38d-6c45-445b-bf56-4d164fcb5d29")
    private String contentUUID;

    @Builder
    public ArucoWithContentUUIDResponse(long aruco, String contentUUID) {
        this.aruco = aruco;
        this.contentUUID = contentUUID;
    }

    @Override
    public String toString() {
        return "ArucoWithContentUUIDResponse{" +
                "aruco=" + aruco +
                ", contentUUID='" + contentUUID + '\'' +
                '}';
    }
}
