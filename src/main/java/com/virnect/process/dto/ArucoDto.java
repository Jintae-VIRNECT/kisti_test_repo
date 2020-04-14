package com.virnect.process.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-01-28
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
public class ArucoDto {
    @Getter
    @Setter
    public static class ArucoInfo {
        private long id;
        private String contentUUID;
    }
}
