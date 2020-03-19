module.exports = {
  /**
   * User
   */
  // 로그인 API
  USER_LOGIN: ['POST', '/users/login'],
  USER_LOGOUT: ['GET', '/users/logout'],
  /**
   * Workspace
   */
  // 워크스페이스 멤버 검색
  MEMBER_LIST: ['GET', '/workspaces/{workspaceId}/members'],
  /**
   * Contents
   */
  // 콘텐츠 통계 조회
  CONTENTS_STATISTICS: ['GET', '/contents/statistics'],
  // 콘텐츠 목록 조회
  CONTENTS_LIST: ['GET', '/contents'],
  // 콘텐츠 삭제 요청
  CONTENT_DELETE: ['DELETE', '/contents/{contentUUID}'],
  // 컨텐츠 상세 정보 조회
  CONTENT_INFO: ['GET', '/contents/{contentUUID}/info'],
  // 씬그룹 목록 조회
  SCENE_GROUPS_LIST: ['GET', '/contents/metadata/sceneGroups'],
  /**
   * Process
   */
  // 공정 통계 조회
  PROCESS_STATISTICS: ['GET', '/processes/statistics'],
  // 전체 공정 목록 조회
  PROCESS_LIST: ['GET', '/processes'],
  // 공정상세조회
  PROCESS_INFO: ['GET', '/processes/{processId}'],
  // 공정편집
  PROCESS_UPDATE: ['POST', '/processes/{processId}'],
  // 공정삭제
  PROCESS_DELETE: ['DELETE', '/processes/{processId}'],
  // 공정마감
  PROCESS_CLOSE: ['PUT', '/processes/{processId}/closed'],
  // 세부공정목록조회
  SUB_PROCESS_LIST: ['GET', '/processes/{processId}/subProcesses'],
  // 이슈 목록을 조회
  ISSUE_LIST: ['GET', '/processes/issues'],
  // 이슈상세조회
  ISSUE_DETAIL: ['GET', '/processes/issue/{issueId}'],
  // 공정생성
  PROCESS_CREATE: ['POST', '/processes/process'],
  // 리포트 목록 조회
  REPORT_LIST: ['GET', '/processes/reports'],
  // 리포트상세조회
  REPORT_DETAIL: ['GET', '/processes/report/{reportId}'],
  // 스마트툴 작업 목록 조회
  SMART_TOOL_LIST: ['GET', '/processes/smartToolJobs'],
  // 워크스페이스의 전체 세부공정목록조회
  SUB_PROCESS_ALL: ['GET', '/processes/subProcesses'],
  // 세부공정 상세조회
  SUB_PROCESS_INFO: ['GET', '/processes/subProcesses/{subProcessId}'],
  // 세부공정편집
  SUB_PROCESS_UPDATE: ['POST', '/processes/subProcesses/{subProcessId}'],
  // 작업목록조회
  JOBS_LIST: ['GET', '/processes/subProcesses/{subProcessId}/jobs'],
}
