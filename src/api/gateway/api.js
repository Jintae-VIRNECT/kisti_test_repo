// const local = 'https://192.168.13.36:8000'
export default {
  /* Account */
  LOGIN: ['POST', 'https://192.168.6.3:8073/auth/signin'],
  TOKEN: ['POST', '/auth/oauth/token'],
  ACCOUNT: ['GET', '/users/info', { type: 'form' }],
  USER_INFO: ['GET', '/users/{userId}'],
  SETTING_INFO: ['GET', '/workspaces/setting', { initing: true }],

  /* LICENSE */
  GET_LICENSE: ['GET', '/licenses/plan/{userId}'],

  /* Workspace - History */
  HISTORY_LIST: ['GET', '/remote/history?userId={userId}'],
  HISTORY_ITEM: ['GET', '/remote/history/{workspaceId}/{sessionId}'],

  ALL_HISTORY_LIST: ['GET', '/remote/dashboard/history/{workspaceId}'],

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

  /* ATTACH */
  FILES: ['GET', '/remote/file'],

  DELETE_FILE_ITEM: [
    'DELETE',
    '/remote/file/{workspaceId}/{sessionId}?objectName={objectName}&userId={userId}',
  ],

  FILE_ITEM_DOWNLOAD_URL: [
    'GET',
    '/remote/file/download/url/{workspaceId}/{sessionId}',
  ],

  /* LOCAL RECORD FILE */
  LOCAL_RECORD_FILES: ['GET', '/remote/file/record/info'],

  LOCAL_RECORD_FILE_DOWNLOAD_URL: [
    'GET',
    '/remote/file/record/download/url/{workspaceId}/{sessionId}',
  ],

  //로컬 녹화 삭제 API 추가하여야함

  // DELETE_RECORD_FILE_ITEM:[
  //   'DELETE',
  //   '/remote/record/'
  // ]

  /** CHART */
  DAILY_COLLABO: ['GET', '/remote/dashboard/history/count/date/{workspaceId}'],

  MONTHLY_COLLABO: [
    'GET',
    '/remote/dashboard/history/count/month/{workspaceId}',
  ],

  /* USER */
  USER_LIST: ['GET'],

  /* WORKSPACE - MEMBER */
  MEMBER_INFO: ['GET', '/workspaces/{workspaceId}/members/info'],
}
