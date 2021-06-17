package com.virnect.data.dto.request.company;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.data.domain.session.SessionType;

@Getter
@Setter
@ApiModel
@NoArgsConstructor
public class CompanyResponse {
    @ApiModelProperty(value = "Workspace Identifier", example = "40f9bbee9d85dca7a34a0dd205aae718")
    @NotNull
    private String workspaceId;

    @ApiModelProperty(value = "Remote Session Type", position = 1)
    private String licenseName;

    @ApiModelProperty(value = "Remote Session Type", position = 2)
    @Enumerated(EnumType.STRING)
    private SessionType sessionType;
}
