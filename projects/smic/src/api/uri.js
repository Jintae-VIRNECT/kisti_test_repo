module.exports = {
  /**
   * Workspace
   */
  // 워크스페이스 멤버 검색
  MEMBER_LIST: ['GET', '/workspaces/{workspaceId}/members'],
  /**
   * Contents
   */
  // 콘텐츠 목록 조회
  CONTENTS_LIST: ['GET', '/contents'],
  // 콘텐츠 삭제 요청
  CONTENT_DELETE: ['DELETE', '/contents/{contentUUID}'],
  // 컨텐츠 상세 정보 조회
  CONTENT_INFO: ['GET', '/contents/{contentUUID}/info'],
  // 씬그룹 목록 조회
  SCENE_GROUP_LIST: ['GET', '/contents/metadata/sceneGroups'],
  /**
   * Process
   */
  // 전체 공정 목록 조회
  PROCESS_LIST: ['GET', '/processes'],
  // 공정상세조회
  PROCESS_DETAIL: ['GET', '/processes/{processId}'],
  // 공정편집
  PROCESS_UPDATE: ['POST', '/processes/{processId}'],
  // 공정삭제
  PROCESS_DELETE: ['DELETE', '/processes/{processId}'],
  // 공정마감
  4: ['PUT', '/processes/{processId}/closed'],
  // 세부공정목록조회
  SUB_PROCESS_LIST: ['GET', '/processes/{processId}/subProcesses'],
  // ARUCO 발급
  6: ['GET', '/processes/aruco'],
  // ARUCO 회수
  7: ['POST', '/processes/aruco'],
  // 컨텐츠 파일의 공정 조회
  8: ['GET', '/processes/content/{contentUUID}'],
  // 이슈 목록을 조회
  ISSUE_LIST: ['GET', '/processes/issues'],
  // 내 작업(나에게 할당된 세부공정) 목록 조회
  10: ['GET', '/processes/myWorks/{workerUUID}'],
  // 신규 할당된 세부공정 유무 조회
  11: ['GET', '/processes/newWork'],
  // 공정생성
  PROCESS_CREATE: ['POST', '/processes/process'],
  // 리포트 목록 조회
  REPORT_LIST: ['GET', '/processes/reports'],
  // 스마트툴 작업 목록 조회
  SMART_TOOL_LIST: ['GET', '/processes/smartToolJobs'],
  // 워크스페이스의 전체 세부공정목록조회
  SUB_PROCESS_ALL: ['GET', '/processes/subProcesses'],
  // 세부공정 상세조회
  SUB_PROCESS_DETAIL: ['GET', '/processes/subProcesses/{subProcessId}'],
  // 세부공정편집
  17: ['POST', '/processes/subProcesses/{subProcessId}'],
  // 작업목록조회
  JOBS_LIST: ['GET', '/processes/subProcesses/{subProcessId}/jobs'],
}
