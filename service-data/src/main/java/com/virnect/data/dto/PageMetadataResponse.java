package com.virnect.data.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PageMetadataResponse {
    @ApiModelProperty(value = "Page number currently viewed", notes = "Current page number", example = "0")
    @Builder.Default private int currentPage = 1;
    @ApiModelProperty(value = "Current number of data per page", notes = "Requested number of data per page", example = "2")
    @Builder.Default private int currentSize = 1;
    @ApiModelProperty(value = "Number of data currently returned", notes = "Returned number of data per page", example = "2")
    @Builder.Default private int numberOfElements = 0;
    @ApiModelProperty(value = "Total number of page", notes = "Total page size", example = "10")
    @Builder.Default private int totalPage = 0;
    @ApiModelProperty(value = "Total number of data", notes = "Total data size", example = "20")
    @Builder.Default private long totalElements = 0;
    @ApiModelProperty(value = "Whether the last page", notes = "check last page", example = "false")
    @Builder.Default private boolean last = true;

    @Override
    public String toString() {
        return "PageMetadataResponse{" +
            "currentPage='" + currentPage + '\'' +
            ", currentSize='" + currentSize + '\'' +
            ", numberOfElements='" + numberOfElements + '\'' +
            ", totalPage='" + totalPage + '\'' +
            ", totalElements='" + totalElements + '\'' +
            ", last='" + last + '\'' +
            '}';
    }

}
