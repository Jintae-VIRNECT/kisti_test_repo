package com.virnect.process.domain;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-01-28
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Item Category enumerated class
 */
public enum ItemType {
    TOGGLE,
    INPUT_FIELD,
    PHOTO,      // 리포트와 같다고 함. 곽승호님 확인
    REPORT,     // 이걸 지우면 오류발생. findByReport와 연관성이 있는 듯한데 확인 필요. 아이템 종류는 toggle, input field, report임.
    NONE;

    @Override
    public String toString() {
        return "ItemType{}";
    }
}
