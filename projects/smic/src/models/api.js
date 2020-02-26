export default {
  // auth
  USER_LOGIN: () => '/users/login',
  USER_LOGOUT: () => '/users/logout',
  // members
  MEMBER_LIST: () => '@workspace/members',
  // contents
  CONTENTS_LIST: () => '@contents',
  CONTENT_DETAIL: contentId => `@contents/${contentId}`,
  CONTENT_INFO: contentId => `@contents/${contentId}/info`,
  // scene group
  SCENE_GROUP_LIST: () => '@contents/metadata/sceneGroups',
  // process
  PROCESS_LIST: () => '@process',
  PROCESS_DETAIL: processId => `@process/${processId}`,
  PROCESS_CREATE: () => `@process/process`,
  // sub process
  SUB_PROCESS_LIST: processId => `@process/${processId}/subProcesses`,
  SUB_PROCESS_DETAIL: subProcessesId =>
    `@process/subProcesses/${subProcessesId}`,
  // jobs
  JOBS_LIST: subProcessesId => `@process/subProcesses/${subProcessesId}/jobs`,
  // issue
  ISSUE_LIST: () => '@process/issues',
  ISSUE_DETAIL: issueId => `@process/issue/${issueId}`,
  // report
  REPORT_LIST: () => `@process/reports`,
  REPORT_DETAIL: reportId => `@process/reports/${reportId}`,
  // smart tool
  SMART_TOOL_LIST: () => `@process/smartToolJobs`,
  SMART_TOOL_DETAIL: smartToolId => `@process/smartToolJobs/${smartToolId}`,
}
