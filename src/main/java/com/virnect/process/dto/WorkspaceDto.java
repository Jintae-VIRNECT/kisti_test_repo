package com.virnect.process.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-01-28
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
public class WorkspaceDto {
    @Getter
    @Setter
    public static class WorkspaceInfo {
        private List<ProcessDto> tasks;

        @Override
        public String toString() {
            return "WorkspaceInfo{" +
                    ", tasks=" + tasks +
                    '}';
        }
    }
}
