package com.virnect.remote.dto.request.room;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class ModifyRoomInfoRequest {
    @NotBlank
    @ApiModelProperty(value = "User Unique Identifier", notes = "Only Remote Session leader allowed.")
    private String uuid;

    @NotBlank
    @ApiModelProperty(value = "To be changed Remote Session Title", position = 1, example = "Changed Title")
    private String title;

    @ApiModelProperty(value = "To be changed Remote Session Description", position = 2, example = "Changed Description")
    private String description;

    @Override
    public String toString() {
        return "ModifyRoomInfoRequest{" +
            "uuid='" + uuid + '\'' +
            ", title='" + title + '\'' +
            ", description='" + description + '\'' +
            '}';
    }
}
