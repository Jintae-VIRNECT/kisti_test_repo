package com.virnect.process.domain;

import lombok.ToString;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-01-28
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Details Process State Enumerated Class
 */
@ToString
public enum State {
    /*
    대상(공정 등)의 생명주기(life-cycle) 중의 상태
     */
    CREATED("최초생성됨"),
    UPDATED("업데이트됨"),
    CLOSED("종료됨"),
    DELETED("제거됨");

    private String message;

    State(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
