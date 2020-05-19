module.exports = {
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
  // 워크스페이스 멤버 검색
  MEMBER_LIST: ['GET', '/workspaces/{workspaceId}/members'],
  // 워크스페이스 멤버 전체 리스트
  MEMBER_LIST_ALL: ['GET', '/workspaces/{workspaceId}/members/simple'],
  // 워크스페이스 사용자 - 멤버 권한 설정
  MEMBER_ROLE_UPDATE: ['POST', '/workspaces/{workspaceId}/members/info'],
  // 워크스페이스 사용자 - 멤버 내보내기
  MEMBER_KICK: ['DELETE', '/workspaces/{workspaceId}/members/info'],
  // 워크스페이스 사용자 - 멤버 초대하기
  MEMBERS_INVITE: ['POST', '/workspaces/{workspaceId}/invite'],
  /**
   * Contents
   */
  // 콘텐츠 통계 조회
  CONTENTS_STATISTICS: ['GET', '/contents/statistics'],
  // 콘텐츠 목록 조회
  CONTENTS_LIST: ['GET', '/contents'],
  // 내 콘텐츠 목록 조회
  CONTENTS_LIST_MINE: ['GET', '/contents/my/{userUUID}'],
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
  // 전체 작업 목록 조회
  TASK_LIST: ['GET', '/tasks'],
  // 작업상세조회
  TASK_INFO: ['GET', '/tasks/{taskId}'],
  // 작업편집
  TASK_UPDATE: ['POST', '/tasks/{taskId}'],
  // 작업삭제
  TASK_DELETE: ['DELETE', '/tasks/{taskId}'],
  // 작업마감
  TASK_CLOSE: ['PUT', '/tasks/{taskId}/closed'],
  // 하위작업목록조회
  SUB_TASK_LIST: ['GET', '/tasks/{taskId}/subTasks'],
  // 이슈 목록을 조회
  ISSUE_LIST: ['GET', '/tasks/issues'],
  // 이슈상세조회
  ISSUE_INFO: ['GET', '/tasks/issue/{issueId}'],
  // 작업생성
  TASK_CREATE: ['POST', '/tasks/task'],
  // 리포트 목록 조회
  REPORT_LIST: ['GET', '/tasks/reports'],
  // 리포트상세조회
  REPORT_INFO: ['GET', '/tasks/report/{reportId}'],
  // 스마트툴 단계 목록 조회
  SMART_TOOL_LIST: ['GET', '/tasks/smartToolSteps'],
  // 워크스페이스의 전체 하위작업목록조회
  SUB_TASK_ALL: ['GET', '/tasks/subTasks'],
  // 하위작업 상세조회
  SUB_TASK_INFO: ['GET', '/tasks/subTasks/{subTaskId}'],
  // 하위작업편집
  SUB_TASK_UPDATE: ['POST', '/tasks/subTasks/{subTaskId}'],
  // 단계목록조회
  JOBS_LIST: ['GET', '/tasks/subTasks/{subTaskId}/steps'],
}
