package com.virnect.data.domain.member;

public enum MemberStatus {
    //ONLINE,
    //OFFLINE,
    LOAD, //원격협업 방에 참여 중
    UNLOAD, //원격협업 방에 미 참여 중
    LOADING, // 원격협업 방에 참여 진행 중
    EVICTED, // 원격협업 방에서 퇴출된 상태

}
