package com.virnect.workspace.application.workspaceuser;

import com.virnect.workspace.domain.setting.SettingValue;
import com.virnect.workspace.domain.workspace.Role;
import com.virnect.workspace.dto.request.WorkspaceInviteRequest;
import com.virnect.workspace.exception.WorkspaceException;
import com.virnect.workspace.global.error.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Project: PF-Workspace
 * DATE: 2021-07-16
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
class WorkspaceUserServiceTest {
    @Test
    @DisplayName("워크스페이스 초대 - 마스터로 권한 부여해서 초대 할 수 없습니다.")
    void workspaceInvite_MasterInvite() {
        //given
        WorkspaceInviteRequest workspaceInviteRequest = new WorkspaceInviteRequest();
        WorkspaceInviteRequest.UserInfo userInfo = new WorkspaceInviteRequest.UserInfo();
        userInfo.setEmail("abc@abc.com");
        userInfo.setRole(Role.MASTER);
        userInfo.setPlanRemote(false);
        userInfo.setPlanMake(false);
        userInfo.setPlanView(false);
        List<WorkspaceInviteRequest.UserInfo> userInfoList = new ArrayList<>();
        userInfoList.add(userInfo);
        workspaceInviteRequest.setUserId("master_user_id");
        workspaceInviteRequest.setUserInfoList(userInfoList);

        //then
        Assertions.assertTrue(workspaceInviteRequest.existMasterUserInvite());
    }


    @DisplayName("워크스페이스 유저 역할 변경 - 역할 변경 권한 체크 - 성공케이스")
    @ParameterizedTest
    @MethodSource("paramsForTestInvalidInputs2")
    void workspaceUserRoleUpdate_DoesNotThrows(Optional<WorkspaceCustomSetting> workspaceCustomSettingOptional, WorkspaceRole requestUserRole, WorkspaceRole updateUserRole) {
        assertDoesNotThrow(() -> {
            if (updateUserRole.getRole().equals("MASTER")) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
            }
            //상위 유저에 대해서는 역할을 변경할 수 없음.
            if (requestUserRole.getId() >= updateUserRole.getId()) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
            }

            //워크스페이스 설정 정보에 따라 권한 체크가 달라짐.
            //Optional<com.virnect.workspace.domain.setting.WorkspaceCustomSetting> workspaceCustomSettingOptional = workspaceCustomSettingRepository.findByWorkspace_UuidAndSetting_Name(workspaceId, SettingName.USER_ROLE_MANAGEMENT_ROLE_SETTING);
            if (!workspaceCustomSettingOptional.isPresent() || workspaceCustomSettingOptional.get().getValue() == SettingValue.UNUSED || workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER) {
                //요청 유저 권한 체크 -> 마스터가 아니면 던짐
                if (!requestUserRole.getRole().equals("MASTER")) {
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                }
            }
            if (workspaceCustomSettingOptional.isPresent()) {
                //log.info("[REVISE MEMBER INFO] workspace custom setting value : [{}]", workspaceCustomSettingOptional.get().getValue());
                //요청 유저 권한 체크 -> 마스터 또는 매니저가 아니면 던짐
                if (workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER_OR_MANAGER && !requestUserRole.getRole().matches("MASTER|MANAGER")) {
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                }
                //요청 유저 권한 체크 -> 마스터 또는 매니저 또는 멤버가 아니면 던짐
                if (workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER_OR_MANAGER_OR_MEMBER && !requestUserRole.getRole().matches("MASTER|MANAGER|MEMBER")) {
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                }
            }
        });

    }

    @DisplayName("워크스페이스 유저 역할 변경 - 역할 변경 권한 체크 - 실패케이스")
    @ParameterizedTest
    @MethodSource("paramsForTestInvalidInputs")
    void workspaceUserRoleUpdate_Throws(Optional<WorkspaceCustomSetting> workspaceCustomSettingOptional, WorkspaceRole requestUserRole, WorkspaceRole updateUserRole) {
        assertThrows(WorkspaceException.class, () -> {
            if (updateUserRole.getRole().equals("MASTER")) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
            }
            //상위 유저에 대해서는 역할을 변경할 수 없음.
            if (requestUserRole.getId() >= updateUserRole.getId()) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
            }

            //워크스페이스 설정 정보에 따라 권한 체크가 달라짐.
            //Optional<com.virnect.workspace.domain.setting.WorkspaceCustomSetting> workspaceCustomSettingOptional = workspaceCustomSettingRepository.findByWorkspace_UuidAndSetting_Name(workspaceId, SettingName.USER_ROLE_MANAGEMENT_ROLE_SETTING);
            if (!workspaceCustomSettingOptional.isPresent() || workspaceCustomSettingOptional.get().getValue() == SettingValue.UNUSED || workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER) {
                //요청 유저 권한 체크 -> 마스터가 아니면 던짐
                if (!requestUserRole.getRole().equals("MASTER")) {
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                }
            }
            if (workspaceCustomSettingOptional.isPresent()) {
                //log.info("[REVISE MEMBER INFO] workspace custom setting value : [{}]", workspaceCustomSettingOptional.get().getValue());
                //요청 유저 권한 체크 -> 마스터 또는 매니저가 아니면 던짐
                if (workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER_OR_MANAGER && !requestUserRole.getRole().matches("MASTER|MANAGER")) {
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                }
                //요청 유저 권한 체크 -> 마스터 또는 매니저 또는 멤버가 아니면 던짐
                if (workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER_OR_MANAGER_OR_MEMBER && !requestUserRole.getRole().matches("MASTER|MANAGER|MEMBER")) {
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                }
            }
        });
    }

    @Builder
    @Getter
    static class WorkspaceRole {
        private Long id;
        private String role;
        private String description;
    }


    private static Stream<Arguments> paramsForTestInvalidInputs() {
        WorkspaceRole masterUserRole = WorkspaceRole.builder().id(1L).role("MASTER").description("MASTER").build();
        WorkspaceRole managerUserRole = WorkspaceRole.builder().id(2L).role("MANAGER").description("MANAGER").build();
        WorkspaceRole memberUserRole = WorkspaceRole.builder().id(3L).role("MEMBER").description("MEMBER").build();
        /**
         * 요청 : 마스터, 대상 : 마스터 (x)
         * 요청 : 마스터, 대상 : 매니저 (o)
         * 요청 : 마스터, 대상 : 멤버 (o)
         * 요청 : 매니저, 대상 : 마스터 (x)
         * 요청 : 매니저, 대상 : 매니저 (x)
         * 요청 : 매니저, 대상 : 멤버 (o)
         * 요청 : 멤버, 대상 : 마스터 (x)
         * 요청 : 멤버, 대상 : 매니저 (x)
         * 요청 : 멤버, 대상 : 멤버 (x)
         *
         * DEFAULT : 요청 유저는 마스터만 허용한다.
         */
        Optional<WorkspaceCustomSetting> workspaceCustomSettingMaster = Optional.ofNullable(WorkspaceCustomSetting.builder().value(SettingValue.MASTER).build());
        Optional<WorkspaceCustomSetting> workspaceCustomSettingManager = Optional.ofNullable(WorkspaceCustomSetting.builder().value(SettingValue.MASTER_OR_MANAGER).build());
        Optional<WorkspaceCustomSetting> workspaceCustomSettingMember = Optional.ofNullable(WorkspaceCustomSetting.builder().value(SettingValue.MASTER_OR_MANAGER_OR_MEMBER).build());
        //성공케이스는 주석처리. 실패케이스만 테스트
        return Stream.of(
                Arguments.of(workspaceCustomSettingMaster, masterUserRole, masterUserRole),
                //Arguments.of(workspaceCustomSettingMaster, masterUserRole, managerUserRole),
                //Arguments.of(workspaceCustomSettingMaster, masterUserRole, memberUserRole),
                Arguments.of(workspaceCustomSettingMaster, managerUserRole, masterUserRole),
                Arguments.of(workspaceCustomSettingMaster, managerUserRole, managerUserRole),
                Arguments.of(workspaceCustomSettingMaster, managerUserRole, memberUserRole),
                Arguments.of(workspaceCustomSettingMaster, memberUserRole, masterUserRole),
                Arguments.of(workspaceCustomSettingMaster, memberUserRole, managerUserRole),
                Arguments.of(workspaceCustomSettingMaster, memberUserRole, memberUserRole),

                Arguments.of(workspaceCustomSettingManager, masterUserRole, masterUserRole),
                //Arguments.of(workspaceCustomSettingManager, masterUserRole, managerUserRole),
                //Arguments.of(workspaceCustomSettingManager, masterUserRole, memberUserRole),
                Arguments.of(workspaceCustomSettingManager, managerUserRole, masterUserRole),
                Arguments.of(workspaceCustomSettingManager, managerUserRole, managerUserRole),
                //Arguments.of(workspaceCustomSettingManager, managerUserRole, memberUserRole),
                Arguments.of(workspaceCustomSettingManager, memberUserRole, masterUserRole),
                Arguments.of(workspaceCustomSettingManager, memberUserRole, managerUserRole),
                Arguments.of(workspaceCustomSettingManager, memberUserRole, memberUserRole),

                Arguments.of(workspaceCustomSettingMember, masterUserRole, masterUserRole),
                //Arguments.of(workspaceCustomSettingMember, masterUserRole, managerUserRole),
                //Arguments.of(workspaceCustomSettingMember, masterUserRole, memberUserRole),
                Arguments.of(workspaceCustomSettingMember, managerUserRole, masterUserRole),
                Arguments.of(workspaceCustomSettingMember, managerUserRole, managerUserRole),
                //Arguments.of(workspaceCustomSettingMember, managerUserRole, memberUserRole),
                Arguments.of(workspaceCustomSettingMember, memberUserRole, masterUserRole),
                Arguments.of(workspaceCustomSettingMember, memberUserRole, managerUserRole),
                Arguments.of(workspaceCustomSettingMember, memberUserRole, memberUserRole)
        );
    }

    private static Stream<Arguments> paramsForTestInvalidInputs2() {
        WorkspaceRole masterUserRole = WorkspaceRole.builder().id(1L).role("MASTER").description("MASTER").build();
        WorkspaceRole managerUserRole = WorkspaceRole.builder().id(2L).role("MANAGER").description("MANAGER").build();
        WorkspaceRole memberUserRole = WorkspaceRole.builder().id(3L).role("MEMBER").description("MEMBER").build();

        Optional<WorkspaceCustomSetting> workspaceCustomSettingMaster = Optional.ofNullable(WorkspaceCustomSetting.builder().value(SettingValue.MASTER).build());
        Optional<WorkspaceCustomSetting> workspaceCustomSettingManager = Optional.ofNullable(WorkspaceCustomSetting.builder().value(SettingValue.MASTER_OR_MANAGER).build());
        Optional<WorkspaceCustomSetting> workspaceCustomSettingMember = Optional.ofNullable(WorkspaceCustomSetting.builder().value(SettingValue.MASTER_OR_MANAGER_OR_MEMBER).build());
        return Stream.of(
                Arguments.of(workspaceCustomSettingMaster, masterUserRole, managerUserRole),
                Arguments.of(workspaceCustomSettingMaster, masterUserRole, memberUserRole),
                Arguments.of(workspaceCustomSettingManager, masterUserRole, managerUserRole),
                Arguments.of(workspaceCustomSettingManager, masterUserRole, memberUserRole),
                Arguments.of(workspaceCustomSettingManager, managerUserRole, memberUserRole),
                Arguments.of(workspaceCustomSettingMember, masterUserRole, managerUserRole),
                Arguments.of(workspaceCustomSettingMember, masterUserRole, memberUserRole),
                Arguments.of(workspaceCustomSettingMember, managerUserRole, memberUserRole)
        );
    }

    private static Stream<Arguments> for_license_update_invalid_input() {
        /**
         * 요청 : 마스터, 대상 : 마스터 (o)
         * 요청 : 마스터, 대상 : 매니저 (o)
         * 요청 : 마스터, 대상 : 멤버 (o)
         * 요청 : 매니저, 대상 : 마스터 (x)
         * 요청 : 매니저, 대상 : 매니저 (o)
         * 요청 : 매니저, 대상 : 멤버 (o)
         * 요청 : 멤버, 대상 : 마스터 (x)
         * 요청 : 멤버, 대상 : 매니저 (x)
         * 요청 : 멤버, 대상 : 멤버 (o)
         *
         * DEFAULT : 요청 유저는 마스터 또는 매니저만 허용한다.
         */

        WorkspaceRole masterUserRole = WorkspaceRole.builder().id(1L).role("MASTER").description("MASTER").build();
        WorkspaceRole managerUserRole = WorkspaceRole.builder().id(2L).role("MANAGER").description("MANAGER").build();
        WorkspaceRole memberUserRole = WorkspaceRole.builder().id(3L).role("MEMBER").description("MEMBER").build();

        Optional<WorkspaceCustomSetting> workspaceCustomSettingMaster = Optional.ofNullable(WorkspaceCustomSetting.builder().value(SettingValue.MASTER).build());
        Optional<WorkspaceCustomSetting> workspaceCustomSettingManager = Optional.ofNullable(WorkspaceCustomSetting.builder().value(SettingValue.MASTER_OR_MANAGER).build());
        Optional<WorkspaceCustomSetting> workspaceCustomSettingMember = Optional.ofNullable(WorkspaceCustomSetting.builder().value(SettingValue.MASTER_OR_MANAGER_OR_MEMBER).build());

        return Stream.of(
                //Arguments.of(workspaceCustomSettingManager, masterUserRole, masterUserRole),
                //Arguments.of(workspaceCustomSettingManager, masterUserRole, managerUserRole),
                //Arguments.of(workspaceCustomSettingManager, masterUserRole, memberUserRole),
                Arguments.of(workspaceCustomSettingManager, managerUserRole, masterUserRole),
                //Arguments.of(workspaceCustomSettingManager, managerUserRole, managerUserRole),
                //Arguments.of(workspaceCustomSettingManager, managerUserRole, memberUserRole),
                Arguments.of(workspaceCustomSettingManager, memberUserRole, masterUserRole),
                Arguments.of(workspaceCustomSettingManager, memberUserRole, managerUserRole),
                Arguments.of(workspaceCustomSettingManager, memberUserRole, memberUserRole),

                //Arguments.of(workspaceCustomSettingMaster, masterUserRole, masterUserRole),
                //Arguments.of(workspaceCustomSettingMaster, masterUserRole, managerUserRole),
                //Arguments.of(workspaceCustomSettingMaster, masterUserRole, memberUserRole),
                Arguments.of(workspaceCustomSettingMaster, managerUserRole, masterUserRole),
                Arguments.of(workspaceCustomSettingMaster, managerUserRole, managerUserRole),
                Arguments.of(workspaceCustomSettingMaster, managerUserRole, memberUserRole),
                Arguments.of(workspaceCustomSettingMaster, memberUserRole, masterUserRole),
                Arguments.of(workspaceCustomSettingMaster, memberUserRole, managerUserRole),
                Arguments.of(workspaceCustomSettingMaster, memberUserRole, memberUserRole),

                //Arguments.of(workspaceCustomSettingMember, masterUserRole, masterUserRole),
                //Arguments.of(workspaceCustomSettingMember, masterUserRole, managerUserRole),
                //Arguments.of(workspaceCustomSettingMember, masterUserRole, memberUserRole),
                Arguments.of(workspaceCustomSettingMember, managerUserRole, masterUserRole),
                //Arguments.of(workspaceCustomSettingMember, managerUserRole, managerUserRole),
                //Arguments.of(workspaceCustomSettingMember, managerUserRole, memberUserRole),
                Arguments.of(workspaceCustomSettingMember, memberUserRole, masterUserRole),
                Arguments.of(workspaceCustomSettingMember, memberUserRole, managerUserRole)
                //Arguments.of(workspaceCustomSettingMember, memberUserRole, memberUserRole)
        );
    }

    private static Stream<Arguments> for_license_update_valid_input() {
        /**
         * 요청 : 마스터, 대상 : 마스터 (o)
         * 요청 : 마스터, 대상 : 매니저 (o)
         * 요청 : 마스터, 대상 : 멤버 (o)
         * 요청 : 매니저, 대상 : 마스터 (x)
         * 요청 : 매니저, 대상 : 매니저 (o)
         * 요청 : 매니저, 대상 : 멤버 (o)
         * 요청 : 멤버, 대상 : 마스터 (x)
         * 요청 : 멤버, 대상 : 매니저 (x)
         * 요청 : 멤버, 대상 : 멤버 (o)
         *
         * DEFAULT : 요청 유저는 마스터 또는 매니저만 허용한다.
         */

        WorkspaceRole masterUserRole = WorkspaceRole.builder().id(1L).role("MASTER").description("MASTER").build();
        WorkspaceRole managerUserRole = WorkspaceRole.builder().id(2L).role("MANAGER").description("MANAGER").build();
        WorkspaceRole memberUserRole = WorkspaceRole.builder().id(3L).role("MEMBER").description("MEMBER").build();

        Optional<WorkspaceCustomSetting> workspaceCustomSettingMaster = Optional.ofNullable(WorkspaceCustomSetting.builder().value(SettingValue.MASTER).build());
        Optional<WorkspaceCustomSetting> workspaceCustomSettingManager = Optional.ofNullable(WorkspaceCustomSetting.builder().value(SettingValue.MASTER_OR_MANAGER).build());
        Optional<WorkspaceCustomSetting> workspaceCustomSettingMember = Optional.ofNullable(WorkspaceCustomSetting.builder().value(SettingValue.MASTER_OR_MANAGER_OR_MEMBER).build());

        return Stream.of(
                Arguments.of(workspaceCustomSettingManager, masterUserRole, masterUserRole),
                Arguments.of(workspaceCustomSettingManager, masterUserRole, managerUserRole),
                Arguments.of(workspaceCustomSettingManager, masterUserRole, memberUserRole),
                Arguments.of(workspaceCustomSettingManager, managerUserRole, managerUserRole),
                Arguments.of(workspaceCustomSettingManager, managerUserRole, memberUserRole),

                Arguments.of(workspaceCustomSettingMaster, masterUserRole, masterUserRole),
                Arguments.of(workspaceCustomSettingMaster, masterUserRole, managerUserRole),
                Arguments.of(workspaceCustomSettingMaster, masterUserRole, memberUserRole),

                Arguments.of(workspaceCustomSettingMember, masterUserRole, masterUserRole),
                Arguments.of(workspaceCustomSettingMember, masterUserRole, managerUserRole),
                Arguments.of(workspaceCustomSettingMember, masterUserRole, memberUserRole),
                Arguments.of(workspaceCustomSettingMember, managerUserRole, managerUserRole),
                Arguments.of(workspaceCustomSettingMember, managerUserRole, memberUserRole),
                Arguments.of(workspaceCustomSettingMember, memberUserRole, memberUserRole)
        );
    }

    @Builder
    @Getter
    static class WorkspaceCustomSetting {
        private SettingValue value;
    }

    @DisplayName("워크스페이스 유저 플랜 변경 - 플랜 변경 권한 체크 - 실패케이스")
    @ParameterizedTest
    @MethodSource("for_license_update_invalid_input")
    void workspaceUserLicenseUpdate_Throws(Optional<WorkspaceCustomSetting> workspaceCustomSettingOptional, WorkspaceRole requestUserRole, WorkspaceRole updateUserRole) {
        assertThrows(WorkspaceException.class, () -> {
            //상위 유저에 대해서는 플랜을 변경할 수 없음.
            if (requestUserRole.getId() > updateUserRole.getId()) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
            }

            //워크스페이스 설정 정보에 따라 권한 체크가 달라짐.
            //Optional<com.virnect.workspace.domain.setting.WorkspaceCustomSetting> workspaceCustomSettingOptional = workspaceCustomSettingRepository.findByWorkspace_UuidAndSetting_Name(workspaceId, SettingName.USER_PLAN_MANAGEMENT_ROLE_SETTING);
            if (!workspaceCustomSettingOptional.isPresent() || workspaceCustomSettingOptional.get().getValue() == SettingValue.UNUSED || workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER_OR_MANAGER) {
                //요청 유저 권한 체크 -> 마스터 또는 매니저가 아니면 던짐
                if (!requestUserRole.getRole().matches("MASTER|MANAGER")) {
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                }
            }
            if (workspaceCustomSettingOptional.isPresent()) {
                //log.info("[REVISE MEMBER INFO] workspace custom setting value : [{}]", workspaceCustomSettingOptional.get().getValue());
                //요청 유저 권한 체크 -> 마스터가 아니면 던짐
                if (workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER && !requestUserRole.getRole().equals("MASTER")) {
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                }
                //요청 유저 권한 체크 -> 마스터 또는 매니저 또는 멤버가 아니면 던짐
                if (workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER_OR_MANAGER_OR_MEMBER && !requestUserRole.getRole().matches("MASTER|MANAGER|MEMBER")) {
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                }
            }
        });
    }

    @DisplayName("워크스페이스 유저 플랜 변경 - 플랜 변경 권한 체크 - 성공케이스")
    @ParameterizedTest
    @MethodSource("for_license_update_valid_input")
    void workspaceUserLicenseUpdate_DoesNotThrows(Optional<WorkspaceCustomSetting> workspaceCustomSettingOptional, WorkspaceRole requestUserRole, WorkspaceRole updateUserRole) {
        assertDoesNotThrow(() -> {
            //상위 유저에 대해서는 플랜을 변경할 수 없음.
            if (requestUserRole.getId() > updateUserRole.getId()) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
            }

            //워크스페이스 설정 정보에 따라 권한 체크가 달라짐.
            //Optional<com.virnect.workspace.domain.setting.WorkspaceCustomSetting> workspaceCustomSettingOptional = workspaceCustomSettingRepository.findByWorkspace_UuidAndSetting_Name(workspaceId, SettingName.USER_PLAN_MANAGEMENT_ROLE_SETTING);
            if (!workspaceCustomSettingOptional.isPresent() || workspaceCustomSettingOptional.get().getValue() == SettingValue.UNUSED || workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER_OR_MANAGER) {
                //요청 유저 권한 체크 -> 마스터 또는 매니저가 아니면 던짐
                if (!requestUserRole.getRole().matches("MASTER|MANAGER")) {
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                }
            }
            if (workspaceCustomSettingOptional.isPresent()) {
                //log.info("[REVISE MEMBER INFO] workspace custom setting value : [{}]", workspaceCustomSettingOptional.get().getValue());
                //요청 유저 권한 체크 -> 마스터가 아니면 던짐
                if (workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER && !requestUserRole.getRole().equals("MASTER")) {
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                }
                //요청 유저 권한 체크 -> 마스터 또는 매니저 또는 멤버가 아니면 던짐
                if (workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER_OR_MANAGER_OR_MEMBER && !requestUserRole.getRole().matches("MASTER|MANAGER|MEMBER")) {
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                }
            }
        });
    }
}
