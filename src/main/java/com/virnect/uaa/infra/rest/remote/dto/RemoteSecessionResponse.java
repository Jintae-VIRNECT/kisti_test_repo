package com.virnect.uaa.infra.rest.remote.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RemoteSecessionResponse {
    private String uuid;
    private boolean result;
    private LocalDateTime deletedDate;

    public RemoteSecessionResponse(String userId, boolean result, LocalDateTime deletedDate) {
        this.uuid = userId;
        this.result = result;
        this.deletedDate = deletedDate;
    }

    @Override
    public String toString() {
        return "RemoteSecessionResponse{" +
                "uuid='" + uuid + '\'' +
                ", result=" + result +
                ", deletedDate=" + deletedDate +
                '}';
    }
}