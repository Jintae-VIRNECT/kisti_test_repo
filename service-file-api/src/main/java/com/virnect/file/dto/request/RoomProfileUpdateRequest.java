package com.virnect.file.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ApiModel
public class RoomProfileUpdateRequest {
    @NotBlank
    @ApiModelProperty(value = "User Unique Identifier", notes = "Only Remote Session leader allowed.")
    private String uuid;

    @ApiModelProperty(value = "To be changed Remote Session Profile Image file",  dataType = "__file", hidden = true)
    private MultipartFile profile;
}
