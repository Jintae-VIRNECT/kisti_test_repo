package com.virnect.process.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CountSubProcessOnWorkerResponse {
    @ApiModelProperty(value = "작업자 식별자", notes = "작업자의 식별자", example = "449ae69cee53b8a6819053828c94e496")
    private String workerUUID;
    @ApiModelProperty(value = "작업 진행중인 세부공정 수", notes = "작업자에게 할당된 세부공정들 중 작업이 진행중인 세부공정의 개수", position = 1, example = "1")
    private Integer countProgressing;
    @ApiModelProperty(value = "할당된 세부공정 수", notes = "작업자에게 할당된 세부공정의 총 개수", position = 2, example = "5")
    private Integer countAssigned;

    @Builder
    public CountSubProcessOnWorkerResponse(String workerUUID, Integer countProgressing, Integer countAssigned) {
        this.workerUUID = workerUUID;
        this.countProgressing = countProgressing;
        this.countAssigned = countAssigned;
    }

    @Override
    public String toString() {
        return "CountSubProcessOnWorkerResponse{" +
                "workerUUID='" + workerUUID + '\'' +
                ", countProgressing=" + countProgressing +
                ", countAssigned=" + countAssigned +
                '}';
    }
}
