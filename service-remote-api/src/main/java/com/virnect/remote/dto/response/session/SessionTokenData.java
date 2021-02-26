package com.virnect.remote.dto.response.session;

import com.google.gson.JsonObject;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class SessionTokenData {
    @ApiModelProperty(
            value = " the sessionId for which the token should be associated",
            example = "ROUTED")
    private String session;

    @ApiModelProperty(value = "", position = 1, example = "PUBLISHER")
    private String role;

    @ApiModelProperty(
            value = "metadata associated to this token (usually participant's information)",
            position = 2)
    private String data;

    @ApiModelProperty(
            value = "",
            position = 3)
    private JsonObject kurentoOptions = new JsonObject();

    @Override
    public String toString() {
        return "SessionTokenData{" +
                "session='" + session + '\'' +
                ", role='" + role + '\'' +
                ", data='" + data + '\'' +
                ", kurentoOptions='" + kurentoOptions.toString() + '\'' +
                '}';
    }
}
