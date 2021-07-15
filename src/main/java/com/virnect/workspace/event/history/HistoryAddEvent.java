package com.virnect.workspace.event.history;

import com.virnect.workspace.domain.workspace.Workspace;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Project: PF-Workspace
 * DATE: 2021-05-17
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@RequiredArgsConstructor
public class HistoryAddEvent {
    private final String message;
    private final String userId;
    private final Workspace workspace;

    @Override
    public String toString() {
        return "HistoryAddEvent{" +
                "message='" + message + '\'' +
                ", userId='" + userId + '\'' +
                ", workspace=" + workspace.getUuid() +
                '}';
    }
}
