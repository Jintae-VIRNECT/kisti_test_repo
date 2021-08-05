package com.virnect.workspace.domain.setting;

/**
 * Project: PF-Workspace
 * DATE: 2021-06-01
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public enum SettingName {
    //멤버
    //개인 계정 초대, 내보내기
    PUBLIC_USER_MANAGEMENT_ROLE_SETTING(Product.WORKSTATION, SettingValue.MASTER_OR_MANAGER, new SettingValue[]{SettingValue.MASTER_OR_MANAGER, SettingValue.MASTER, SettingValue.MASTER_OR_MANAGER_OR_MEMBER}),
    //전용 계정 생성, 삭제, 정보 편집
    PRIVATE_USER_MANAGEMENT_ROLE_SETTING(Product.WORKSTATION, SettingValue.MASTER_OR_MANAGER, new SettingValue[]{SettingValue.MASTER_OR_MANAGER, SettingValue.MASTER, SettingValue.MASTER_OR_MANAGER_OR_MEMBER}),
    //시트 추가, 삭제, 정보 편집
    SEAT_MANAGEMENT_ROLE_SETTING(Product.WORKSTATION, SettingValue.MASTER_OR_MANAGER, new SettingValue[]{SettingValue.MASTER_OR_MANAGER, SettingValue.MASTER, SettingValue.MASTER_OR_MANAGER_OR_MEMBER}),
    //플랜 할당, 편집
    USER_PLAN_MANAGEMENT_ROLE_SETTING(Product.WORKSTATION, SettingValue.MASTER_OR_MANAGER, new SettingValue[]{SettingValue.MASTER_OR_MANAGER, SettingValue.MASTER, SettingValue.MASTER_OR_MANAGER_OR_MEMBER}),
    USER_ROLE_MANAGEMENT_ROLE_SETTING(Product.WORKSTATION, SettingValue.MASTER, new SettingValue[]{SettingValue.MASTER, SettingValue.MASTER_OR_MANAGER, SettingValue.MASTER_OR_MANAGER_OR_MEMBER}),
    //콘텐츠
    OPEN_CONTENTS_SHARE_SETTING(Product.WORKSTATION, SettingValue.UNUSED, new SettingValue[]{SettingValue.UNUSED, SettingValue.USE}),
    //작업
    TASK_REGISTER_ROLE_SETTING(Product.WORKSTATION, SettingValue.MASTER_OR_MANAGER_OR_MEMBER, new SettingValue[]{SettingValue.MASTER_OR_MANAGER_OR_MEMBER, SettingValue.MASTER_OR_MANAGER, SettingValue.MASTER}),
    TASK_UPDATE_ROLE_SETTING(Product.WORKSTATION, SettingValue.MASTER_OR_MANAGER_OR_MEMBER, new SettingValue[]{SettingValue.MASTER_OR_MANAGER_OR_MEMBER, SettingValue.MASTER_OR_MANAGER, SettingValue.MASTER}),
    TASK_FINISH_ROLE_SETTING(Product.WORKSTATION, SettingValue.MASTER_OR_MANAGER_OR_MEMBER, new SettingValue[]{SettingValue.MASTER_OR_MANAGER_OR_MEMBER, SettingValue.MASTER_OR_MANAGER, SettingValue.MASTER}),
    TASK_DELETE_ROLE_SETTING(Product.WORKSTATION, SettingValue.MASTER_OR_MANAGER_OR_MEMBER, new SettingValue[]{SettingValue.MASTER_OR_MANAGER_OR_MEMBER, SettingValue.MASTER_OR_MANAGER, SettingValue.MASTER}),
    //리모트 협업룸 설정
    COOPERATION_ROOM_PARTICIPANTS_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.TWO, SettingValue.THREE, SettingValue.FOUR, SettingValue.FIVE, SettingValue.SIX}),
    COOPERATION_ROOM_CREATION_ROLE_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.MASTER, SettingValue.MASTER_OR_MANAGER}),
    //리모트 오픈룸 설정
    OPEN_ROOM_USE_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.UNUSED, SettingValue.USE}),
    OPEN_ROOM_CREATION_ROLE_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.MASTER, SettingValue.MASTER_OR_MANAGER}),
    OPEN_ROOM_PARTICIPANTS_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.TWO, SettingValue.THREE, SettingValue.FOUR, SettingValue.FIVE, SettingValue.SIX}),
    SHEET_PARTICIPATION_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.UNUSED, SettingValue.USE}),
    //리모트 참여환경 설정
    MIC_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.FORCE_ON, SettingValue.FORCE_OFF}),
    CAMERA_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.FORCE_ON, SettingValue.FORCE_OFF}),
    SPEAKER_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.FORCE_ON, SettingValue.FORCE_OFF}),
    //영상 품질
    SCREEN_RESOLUTION_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.SCREEN_RESOLUTION_480, SettingValue.SCREEN_RESOLUTION_720, SettingValue.SCREEN_RESOLUTION_1080}),
    FPS_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.FPS_25, SettingValue.FPS_30, SettingValue.FPS_60, SettingValue.FPS_80, SettingValue.FPS_120, SettingValue.FPS_144}),
    BITRATE_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.BITRATE_2500, SettingValue.BITRATE_3500, SettingValue.BITRATE_5000, SettingValue.BITRATE_6000, SettingValue.BITRATE_12000}),
    //로컬 녹화
    LOCAL_RECORD_USE_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.READER, SettingValue.FORBID}),
    LOCAL_RECORD_MAX_DURATION_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.UNUSED, SettingValue.USE}),
    LOCAL_RECORD_SCREEN_RESOLUTION_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.UNUSED, SettingValue.USE}),
    //서버 녹화
    SERVER_RECORD_USE_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.READER, SettingValue.FORBID}),
    SERVER_RECORD_MAX_DURATION_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.UNUSED, SettingValue.USE}),
    SERVER_RECORD_SCREEN_RESOLUTION_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.UNUSED, SettingValue.USE}),
    SERVER_AUTO_RECORD_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.UNUSED, SettingValue.USE}),
    //원격 제어
    CAMERA_REMOTE_CONTROL_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.UNUSED, SettingValue.USE}),
    MIC_REMOTE_CONTROL_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.UNUSED, SettingValue.USE}),
    FLASH_REMOTE_CONTROL_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.UNUSED, SettingValue.USE}),
    SCREEN_SIZE_REMOTE_CONTROL_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.UNUSED, SettingValue.USE}),
    //협업 보드
    COOPERATION_BOARD_MAX_FILE_SIZE_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.SIZE_10MB, SettingValue.SIZE_50MB, SettingValue.SIZE_100MB}),
    //화면 공유
    SCREEN_SHARE_USE_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.UNUSED, SettingValue.USE}),
    //번역
    TRANSLATE_USE_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.UNUSED, SettingValue.USE}),
    //채팅
    CHAT_ATTACHMENT_FILE_USE_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.UNUSED, SettingValue.USE}),
    CHAT_ATTACHMENT_FILE_MAX_SIZE_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.SIZE_10MB, SettingValue.SIZE_50MB, SettingValue.SIZE_100MB}),
    //화면 캡쳐
    SCREEN_CAPTURE_USE_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.UNUSED, SettingValue.USE}),
    //저작 도구
    DRAWING_THICKNESS_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.THICKNESS_1, SettingValue.THICKNESS_2, SettingValue.THICKNESS_3, SettingValue.THICKNESS_6, SettingValue.THICKNESS_10}),
    FONT_SIZE_SETTING(Product.REMOTE, SettingValue.UNUSED, new SettingValue[]{SettingValue.FONT_10, SettingValue.FONT_12, SettingValue.FONT_14, SettingValue.FONT_16, SettingValue.FONT_18});

    private Product product;
    private SettingValue defaultSettingValue;
    private SettingValue[] settingValues;

    SettingName(Product product, SettingValue defaultSettingValue, SettingValue[] settingValues) {
        this.product = product;
        this.defaultSettingValue = defaultSettingValue;
        this.settingValues = settingValues;
    }

    public Product getProduct() {
        return product;
    }

    public SettingValue getDefaultSettingValue() {
        return defaultSettingValue;
    }

    public SettingValue[] getSettingValues() {
        return settingValues;
    }
}
