export default {
  // auth
  USER_LOGIN: () => '/users/login',
  USER_LOGOUT: () => '/users/logout',
  // members
  MEMBER_LIST: () => '@workspace/members',
  // contents
  CONTENTS_LIST: () => '@contents',
  CONTENTS_DETAIL: contentId => `@contents/${contentId}`,
  // scene group
  SCENE_GROUP_LIST: () => '@contents/metadata/sceneGroups',
  // process
  PROCESS_LIST: () => '@process',
  CREATE_PROCESS: () => `@process/process`,
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
