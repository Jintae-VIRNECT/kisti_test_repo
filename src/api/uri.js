export default {
  /**
   * User
   */
  // 로그인 API
  USER_LOGIN: ['POST', '/users/login'],
  USER_LOGOUT: ['GET', '/users/logout'],
  GET_AUTH_INFO: ['GET', '/users/info'],
  /**
   * Workspace
   */
  // 내가 속한 워크스페이스 목록 조회
  WORKSPACES_LIST: ['GET', '/workspaces'],
  // 워크스페이스 홈 - 정보 조회
  WORKSPACE_INFO: ['GET', '/workspaces/home/{workspaceId}'],
  // 워크스페이스 홈 - 신규 멤버 조회
  WORKSPACE_NEW_MEMBERS: ['GET', '/workspaces/home/{workspaceId}/members'],
  // 워크스페이스 시작하기
  WORKSPACE_START: ['POST', '/workspaces'],
  // 워크스페이스 프로필 설정
  WORKSPACE_EDIT: ['PUT', '/workspaces'],
  // 워크스페이스 프로필 설정
  WORKSPACE_LEAVE: ['DELETE', '/workspaces/{workspaceId}/exit'],
  // 워크스페이스 멤버 검색
  MEMBER_LIST: ['GET', '/workspaces/{workspaceId}/members'],
  // 워크스페이스 멤버 전체 리스트
  MEMBER_LIST_ALL: ['GET', '/workspaces/{workspaceId}/members/simple'],
  // 워크스페이스 사용자 상세 조회
  MEMBER_INFO: ['GET', '/workspaces/{workspaceId}/members/info'],
  // 워크스페이스 전용 계정 아이디 조회
  MEMBER_ID_CHECK: ['GET', '/users/members/exist'],
  // 워크스페이스 사용자 정보 설정 (플랜, 닉네임, 역할 )
  MEMBER_INFO_UPDATE: ['POST', '/workspaces/{workspaceId}/members/info'],
  // 워크스페이스 전용계정/게스트계정 프로필 이미지 변경
  MEMBER_PROFILE_UPDATE: ['POST', '/workspaces/{workspaceId}/members/profile'],
  // 워크스페이스 사용자 - 멤버 내보내기
  MEMBER_KICK: ['DELETE', '/workspaces/{workspaceId}/members/info'],
  // 워크세페이스 게스트 삭제
  MEMBER_GUEST_DELETE: ['DELETE', '/workspaces/{workspaceId}/members/guest'],
  // 워크스페이스 게스트 생성
  MEMBER_GUEST_CREATE: ['POST', '/workspaces/{workspaceId}/members/guest'],
  // 워크스페이스 사용자 - 멤버 초대하기
  MEMBERS_INVITE: ['POST', '/workspaces/{workspaceId}/invite'],
  // 워크스페이스 사용자 활동 조회
  MEMBERS_ACTIVITY: ['GET', '/tasks/{workspaceUUID}/info'],
  // 워크스페이스 라이선스 플랜 정보 조회
  GET_WORKSPACE_PLAN_INFO: ['GET', '/licenses/{workspaceId}/plan'],
  /**
   * Contents
   */
  // 콘텐츠 통계 조회
  CONTENTS_STATISTICS: ['GET', '/contents/statistics'],
  // 콘텐츠 목록 조회
  CONTENTS_LIST: ['GET', '/contents'],
  // 콘텐츠 삭제 요청
  CONTENT_DELETE: ['DELETE', '/contents'],
  // 콘텐츠 상태 수정
  CONTENT_UPDATE: ['PUT', '/contents/info/{contentUUID}'],
  // 컨텐츠 상세 정보 조회
  CONTENT_INFO: ['GET', '/contents/{contentUUID}'],
  // 씬그룹 목록 조회
  CONTENT_SCENE_GROUPS: ['GET', '/contents/sceneGroups/content/{contentUUID}'],
  // 컨텐츠 속성 트리 조회
  CONTENT_PROPERTIES: ['GET', '/contents/properties/metadata/{contentUUID}'],
  /**
   * Task
   */
  // 작업 통계 조회
  TASK_STATISTICS: ['GET', '/tasks/statistics'],
  // 전체 작업 진행률 조회
  TASK_TOTAL_RATE: ['GET', '/tasks/totalRate'],
  // 해당월의 일별 통계 조회
  TASK_DAILY: ['GET', '/tasks/dailyTotalRateAtMonth'],
  // 전체 작업 목록 조회
  TASK_LIST: ['GET', '/tasks'],
  // 작업상세조회
  TASK_INFO: ['GET', '/tasks/{taskId}'],
  // 작업생성
  TASK_CREATE: ['POST', '/tasks/task'],
  // 작업 추가생성
  TASK_DUPLICATE: ['POST', '/tasks/duplicate'],
  // 작업편집
  TASK_UPDATE: ['POST', '/tasks/{taskId}'],
  // 작업삭제
  TASK_DELETE: ['DELETE', '/tasks'],
  // 작업마감
  TASK_CLOSE: ['PUT', '/tasks/{taskId}/closed'],
  // 하위작업목록조회
  SUB_TASK_LIST: ['GET', '/tasks/{taskId}/subTasks'],
  // 하위작업 상세조회
  SUB_TASK_INFO: ['GET', '/tasks/subTasks/{subTaskId}'],
  // 하위작업편집
  SUB_TASK_UPDATE: ['POST', '/tasks/subTasks/{subTaskId}'],
  // 단계목록조회
  STEPS_LIST: ['GET', '/tasks/subTasks/{subTaskId}/steps'],
  // 타겟정보조회
  TARGET_INFO: ['GET', '/tasks/task/content/{taskId}'],
  /**
   * results
   */
  // 워크스페이스의 전체 하위작업목록조회
  SUB_TASK_ALL: ['GET', '/tasks/subTasks'],
  // 이슈조회
  ISSUES_ALL: ['GET', '/tasks/issues'],
  // 페이퍼 조회
  PAPERS_ALL: ['GET', '/tasks/papers'],
  // 페이퍼 상세조회
  ISSUE_INFO: ['GET', '/tasks/issue/{issueId}'],
  // 페이퍼 상세조회
  PAPER_INFO: ['GET', '/tasks/paper/{paperId}'],
  // 트러블 메모 목록 조회
  TROUBLES_LIST: ['GET', '/tasks/troubleMemos'],
  // 트러블 메모 상세 조회
  TROUBLE_INFO: ['GET', '/tasks/troubleMemo/{troubleMemoId}'],
  /**
   * Projects
   */
  // 프로젝트 목록 조회
  PROJECTS_LIST: ['GET', '/contents/projects'],
  // 프로젝트 상세 정보 조회
  PROJECT_INFO: ['GET', '/contents/projects/{projectUUID}'],
  // 프로젝트 상태 수정
  PROJECT_UPDATE: ['PUT', '/contents/projects/{projectUUID}'],
  // 프로젝트 삭제 요청
  PROJECT_DELETE: ['DELETE', '/contents/projects/{projectUUID}'],
  // 프로젝트의 활동 이력 조회요청
  PROJECT_ACTIVITIES: ['GET', '/contents/projects/{projectUUID}/activities'],

  /**
   * onpremise
   */
  // 워크스페이스 멤버 계정 생성
  MEMBERS_CREATE: ['POST', '/workspaces/{workspaceId}/members/account'],
  // 워크스페이스 멤버 계정 삭제
  MEMBER_DELETE: ['DELETE', '/workspaces/{workspaceId}/members/account'],
  // 워크스페이스 멤버 비밀번호 재설정
  MEMBER_CHANGE_PASSWORD: [
    'POST',
    '/workspaces/{workspaceId}/members/password',
  ],
  // 워크스페이스 커스텀 설정 조회
  WORKSPACE_GET_SETTING: ['GET', '/workspaces/setting'],
  // 워크스페이스 고객사명 변경
  WORKSPACE_SET_TITLE: ['POST', '/workspaces/{workspaceId}/title'],
  // 워크스페이스 로고 변경
  WORKSPACE_SET_LOGO: ['POST', '/workspaces/{workspaceId}/logo'],
  // 워크스페이스 파비콘 변경
  WORKSPACE_SET_FAVICON: ['POST', '/workspaces/{workspaceId}/favicon'],
}
