// const local = 'https://192.168.13.36:8000'
export default {
  /* Account */
  LOGIN: ['POST', 'https://192.168.6.3:8073/auth/signin'],
  TOKEN: ['POST', '/auth/oauth/token'],
  ACCOUNT: ['GET', '/users/info', { type: 'form' }],
  USER_INFO: ['GET', '/users/{userId}'],

  /* LICENSE */
  GET_LICENSE: ['GET', '/licenses/plan/{userId}'],

  /* Workspace - History */
  HISTORY_LIST: ['GET', '/remote/history?userId={userId}'],
  HISTORY_ITEM: ['GET', '/remote/history/{workspaceId}/{sessionId}'],

  /* SERVER RECORD */
  SERVER_RECORD_FILES: [
    'GET',
    '/remote/recorder/workspaces/{workspaceId}/users/{userId}/files',
  ],

  DELETE_SERVER_RECORD_FILES_ALL: [
    'DELETE',
    '/remote/recorder/workspaces/{workspaceId}/users/{userId}/files',
  ],

  DELETE_SERVER_RECORD_FILES_ITEM: [
    'DELETE',
    '/remote/recorder/workspaces/{workspaceId}/users/{userId}/files/{id}',
  ],

  SERVER_RECORD_FILE_URL: [
    'GET',
    '/remote/recorder/workspaces/{workspaceId}/users/{userId}/files/{id}/url',
  ],

  //서버녹화 파일 URL로 파일 다운로드 시도하는 API...이이이이잌
  // SERVER_RECORD_FILE_URL: [
  //   'GET',
  //   'https://192.168.6.3:8073/remote/recorder/file/download/{id}',
  //   { type: 'application/octet-stream', responseType: 'blob' },
  // ],

  /* ATTACH & LOCAL RECORD FILE */
  FILES: ['GET', '/remote/file'],

  DELETE_FILE_ITEM: ['DELETE', '/remote/file/{workspaceId}/{sessionId}'],

  DOWNLOAD_FILE_ITEM: [
    'GET',
    '/remote/file/download/{workspaceId}/{sessionId}',
  ],

  FILE_ITEM_DOWNLOAD_URL: [
    'GET',
    '/remote/file/download/url/{workspaceId}/{sessionId}',
  ],

  /* USER */
  USER_LIST: ['GET'],
}
