module.exports = {
  /**
   * User
   */
  GET_AUTH_INFO: ['GET', '/users/info'],
  /**
   * Download
   */
  APP_LIST: ['GET', '/download/list/{productName}'],
  DOWNLOAD_APP: ['GET', '/download/app/{uuid}'],
  DOWNLOAD_GUIDE: ['GET', '/download/guide/{uuid}'],

  /**
   * onpremise
   */
  // 워크스페이스 커스텀 설정 조회
  WORKSPACE_GET_SETTING: ['GET', '/workspaces/setting'],
  // 워크스페이스 고객사명 변경
  WORKSPACE_SET_TITLE: ['POST', '/workspaces/{workspaceId}/title'],
  // 워크스페이스 로고 변경
  WORKSPACE_SET_LOGO: ['POST', '/workspaces/{workspaceId}/logo'],
  // 워크스페이스 파비콘 변경
  WORKSPACE_SET_FAVICON: ['POST', '/workspaces/{workspaceId}/favicon'],
}
