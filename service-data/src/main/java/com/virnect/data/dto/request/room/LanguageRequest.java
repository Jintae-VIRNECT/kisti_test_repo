package com.virnect.data.dto.request.room;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class LanguageRequest {
    @ApiModelProperty(value = "Enable Translation Language", example = "false")
    private boolean transKoKr;

    @ApiModelProperty(value = "Enable Translation Language", position = 1, example = "false")
    private boolean transEnUs;

    @ApiModelProperty(value = "Enable Translation Language", position = 2, example = "false")
    private boolean transJaJp;

    @ApiModelProperty(value = "Enable Translation Language", position = 3, example = "false")
    private boolean transZh;

    @ApiModelProperty(value = "Enable Translation Language", position = 4, example = "false")
    private boolean transFrFr;

    @ApiModelProperty(value = "Enable Translation Language", position = 5, example = "false")
    private boolean transEsEs;

    @ApiModelProperty(value = "Enable Translation Language", position = 6, example = "false")
    private boolean transRuRu;

    @ApiModelProperty(value = "Enable Translation Language", position = 7, example = "false")
    private boolean transUkUa;

    @ApiModelProperty(value = "Enable Translation Language", position = 8, example = "false")
    private boolean transPlPl;

    @ApiModelProperty(value = "Enable Translation Language", position = 9, example = "false")
    private boolean transThTh;
}
