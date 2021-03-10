export default {
  /* ACCOUNT */
  LOGIN: ['POST', 'https://192.168.6.3:8073/auth/signin'],
  TOKEN: ['POST', '/auth/oauth/token'],
  ACCOUNT: ['GET', '/users/info', { type: 'form' }],
  USER_INFO: ['GET', '/users/{userId}'],
  SETTING_INFO: ['GET', '/workspaces/setting', { initing: true }],

  /* LICENSE */
  GET_LICENSE: ['GET', '/licenses/plan/{userId}?size=30'],
  COMPANY_INFO: ['GET', '/remote/company/{workspaceId}/{userId}'],

  /* HISTORY */
  HISTORY_LIST: ['GET', '/remote/dashboard/my-history/{workspaceId}/{userId}'],
  HISTORY_ITEM: ['GET', '/remote/dashboard/history/{workspaceId}/{sessionId}'],
  ALL_HISTORY_LIST: ['GET', '/remote/dashboard/history/{workspaceId}'],

  /* HISTORY - ROOM */
  ROOM_INFO: ['GET', '/remote/dashboard/room/{workspaceId}/{sessionId}'],

  /* ATTACH FILE */
  FILES: ['GET', '/remote/dashboard/file/{workspaceId}/{sessionId}'],
  DELETE_FILE_ITEM: [
    'DELETE',
    '/remote/dashboard/file/{workspaceId}/{sessionId}?objectName={objectName}&userId={userId}',
  ],
  FILE_ITEM_DOWNLOAD_URL: [
    'GET',
    '/remote/dashboard/file/url/{workspaceId}/{sessionId}',
  ],

  /* SERVER RECORD FILE */
  SERVER_RECORD_FILES: [
    'GET',
    '/remote/dashboard/file/record/{workspaceId}/{userId}',
  ],

  DELETE_SERVER_RECORD_FILES_ITEM: [
    'DELETE',
    '/remote/dashboard/file/record/{workspaceId}?id={id}&userId={userId}',
  ],

  SERVER_RECORD_FILE_URL: [
    'GET',
    '/remote/dashboard/file/record/workspaces/{workspaceId}/users/{userId}/files/{id}/url',
  ],

  /* LOCAL RECORD FILE */
  LOCAL_RECORD_FILES: [
    'GET',
    '/remote/dashboard/file/record-file/{workspaceId}/{sessionId}',
  ],
  LOCAL_RECORD_FILE_DOWNLOAD_URL: [
    'GET',
    '/remote/dashboard/file/record-file/url/{workspaceId}/{sessionId}',
  ],
  DELETE_RECORD_FILE_ITEM: [
    'DELETE',
    '/remote/dashboard/file/record-file/{workspaceId}/{sessionId}?objectName={objectName}&userId={userId}',
  ],

  /** CHART */
  DAILY_COLLABO: [
    'GET',
    '/remote/dashboard/history/count/date/{workspaceId}/{userId}',
  ],

  MONTHLY_COLLABO: [
    'GET',
    '/remote/dashboard/history/count/month/{workspaceId}/{userId}',
  ],

  /* USER */
  USER_LIST: ['GET'],

  /* WORKSPACE - MEMBER */
  MEMBER_INFO: ['GET', '/workspaces/{workspaceId}/members/info'],
}
