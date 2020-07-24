package com.virnect.content.dto.rest;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class ProcessInfoResponse {
    @NotBlank
    @ApiModelProperty(value = "공정식별자", notes = "공정식별자", example = "1")
    private long id;

    @NotBlank
    @ApiModelProperty(value = "공정명", notes = "공정명", position = 1, example = "자제 절단 세부공정")
    private String name;

    @NotBlank
    @ApiModelProperty(value = "컨텐츠 고유 식별자", notes = "해당 식별자를 통해 컨텐츠를 구별합니다.", position = 2, example = "061cc38d-6c45-445b-bf56-4d164fcb5d29")
    private String contentUUID;

    @Builder
    public ProcessInfoResponse(@NotBlank long id, @NotBlank String name, @NotBlank String contentUUID) {
        this.id = id;
        this.name = name;
        this.contentUUID = contentUUID;
    }
}
