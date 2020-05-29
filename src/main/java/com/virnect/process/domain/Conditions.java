package com.virnect.process.domain;

import lombok.ToString;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-01-28
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Details Process Conditions Enumerated Class
 */
@ToString
public enum Conditions {
    /*
    공정 하위의 세부공정 / 작업의 상태와 일정을 고려한 공정의 상태.
    스케줄러에 의하여 상태값이 주기적으로 갱신됨.
     */
    ALL("전체"),
    WAIT("대기"),
    UNPROGRESSING("미진행"),
    PROGRESSING("진행중"),
    COMPLETED("완료"),
    INCOMPLETED("미흡"),
    FAILED("미완수"),
    SUCCESS("완수"),
    FAULT("결함"),
    NONE("상태없음");


    private String message;

    Conditions(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
