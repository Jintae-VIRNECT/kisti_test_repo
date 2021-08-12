package com.virnect.workspace.dto.rest;

import lombok.Getter;
import lombok.Setter;

/**
 * Project: PF-Workspace
 * DATE: 2021-08-12
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class SeatMemberDeleteRequest {
    private String masterUUID;
    private String seatUserUUID;

    @Override
    public String toString() {
        return "SeatMemberDeleteRequest{" +
                "masterUUID='" + masterUUID + '\'' +
                ", seatUserUUID='" + seatUserUUID + '\'' +
                '}';
    }
}
