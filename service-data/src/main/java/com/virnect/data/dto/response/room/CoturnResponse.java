package com.virnect.data.dto.response.room;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CoturnResponse {
    @ApiModelProperty(value = "coturn user name")
    private String username;
    @ApiModelProperty(value = "coturn user credential")
    private String credential;
    @ApiModelProperty(value = "coturn address")
    private String url;

    @Override
    public String toString() {
        return "Coturn{" +
                "username='" + username + '\'' +
                ", credential='" + credential + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
