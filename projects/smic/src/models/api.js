/**
 * 상수 이름에 _RUD는 적지 않음 (REST 메소드로 구분)
 */
export default {
  // auth
  USER_LOGIN: () => '/users/login',
  USER_LOGOUT: () => '/users/logout',
  // members
  MEMBER_LIST: () => '@workspace/members',
  // contents
  CONTENTS_LIST: () => '@contents',
  CONTENT_DETAIL: contentId => `@contents/${contentId}`,
  // scene group
  SCENE_GROUP_LIST: () => '@contents/metadata/sceneGroups',
  // process
  PROCESS_LIST: () => '@process',
  PROCESS_DETAIL: processId => `@process/${processId}`,
  PROCESS_CREATE: () => `@process/process`,
  // sub process
  SUB_PROCESS_LIST: processId => `@process/${processId}/subProcesses`,
  // jobs
  JOBS_LIST: subProcessesId => `@process/subProcesses/${subProcessesId}/jobs`,
  // issue
  ISSUE_LIST: () => '@process/issues',
  ISSUE_DETAIL: issueId => `@process/issue/${issueId}`,
  // report
  REPORT_DETAIL: reportId => `@process/report/${reportId}`,
}
