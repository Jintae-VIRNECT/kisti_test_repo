package com.virnect.data.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PageMetadataResponse {
    @ApiModelProperty(value = "Page number currently viewed", notes = "Current page number", example = "0")
    private int currentPage;
    @ApiModelProperty(value = "Current number of data per page", notes = "Requested number of data per page", example = "2")
    private int currentSize;
    @ApiModelProperty(value = "Number of data currently returned", notes = "Returned number of data per page", example = "2")
    private int numberOfElements;
    @ApiModelProperty(value = "Total number of page", notes = "Total page size", example = "10")
    private int totalPage;
    @ApiModelProperty(value = "Total number of data", notes = "Total data size", example = "20")
    private long totalElements;
    @ApiModelProperty(value = "Whether the last page", notes = "check last page", example = "false")
    private boolean last;

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
