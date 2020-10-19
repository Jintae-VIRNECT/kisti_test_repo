export default {
  /* Account */
  LOGIN: ['POST', 'https://192.168.6.3:8073/auth/signin'],
  TOKEN: ['POST', '/auth/oauth/token'],
  ACCOUNT: ['GET', '/users/info', { type: 'form' }],
  USER_INFO: ['GET', '/users/{userId}'],
  SETTING_INFO: ['GET', '/workspace/setting'],

  /* LICENSE */
  GET_LICENSE: ['GET', '/licenses/plan/{userId}?size=30'],
  COMPANY_INFO: ['GET', '/remote/company/{workspaceId}/{userId}'],

  /* Workspace - History */
  HISTORY_LIST: ['GET', '/remote/history?userId={userId}'],
  HISTORY_ITEM: ['GET', '/remote/history/{workspaceId}/{sessionId}'],
  DELETE_HISTORY_ITEM: ['DELETE', '/remote/history/{workspaceId}'],
  DELETE_HISTORY_ALL: ['DELETE', '/remote/history/{workspaceId}/{userId}'],

  /* Workspace - Member */
  MEMBER_LIST: ['GET', '/remote/members/{workspaceId}/{userId}'],
  INVITABLE_MEMBER_LIST: [
    'GET',
    '/remote/members/{workspaceId}/{sessionId}/{userId}',
  ],

  /* Workspace - Room */
  ROOM_LIST: ['GET', '/remote/room?workspaceId={workspaceId}'],
  CREATE_ROOM: ['POST', '/remote/room?companyCode={companyCode}'],
  RESTART_ROOM: [
    'POST',
    '/remote/history?companyCode={companyCode}&sessionId={sessionId}',
  ],
  UPDATE_ROOM_PROFILE: [
    'POST',
    '/remote/file/{workspaceId}/{sessionId}/profile',
    { type: 'form' },
  ],
  JOIN_ROOM: ['POST', '/remote/room/{workspaceId}/{sessionId}/join'],
  ROOM_INFO: ['GET', '/remote/room/{workspaceId}/{sessionId}'],
  DELETE_ROOM: ['DELETE', '/remote/room/{workspaceId}/{sessionId}/{userId}'],
  LEAVE_ROOM: [
    'DELETE',
    '/remote/room/{workspaceId}/{sessionId}/exit?userId={userId}',
  ],
  UPDATE_ROOM_INFO: ['POST', '/remote/room/{workspaceId}/{sessionId}/info'],

  /* CALL */
  INVITE_ROOM: ['POST', '/remote/room/{workspaceId}/{sessionId}/member'],
  KICKOUT_MEMBER: ['DELETE', '/remote/room/{workspaceId}/{sessionId}/member'],
  SEND_SIGNAL: ['POST', '/remote/room/{workspaceId}/signal'],

  /* MESSAGE */
  SEND_PUSH: ['POST', '/remote/message/push'],

  /* CHAT FILE */
  SEND_FILE: [
    'POST',
    'https://192.168.13.94:4443/file/upload',
    { type: 'form' },
  ],
  FILE_UPLOAD: ['POST', '/remote/file/upload', { type: 'form' }],
  // FILE_DOWNLOAD: [
  //   'GET',
  //   '/remote/file/download/{workspaceId}/{sessionId}',
  //   { response: 'direct', type: 'blob' },
  // ],
  FILE_DOWNLOAD: ['GET', '/remote/file/download/url/{workspaceId}/{sessionId}'],
  FILE_LIST: ['GET', '/remote/file'],

  /* SERVER RECORD */
  START_SERVER_RECORD: [
    'POST',
    '/remote/recorder/workspaces/{workspaceId}/users/{userId}/recordings',
  ],
  STOP_SERVER_RECORD: [
    'DELETE',
    '/remote/recorder/workspaces/{workspaceId}/users/{userId}/recordings/{id}',
  ],
}

export const wsUri = {
  REMOTE: '/remote/websocket',
  MESSAGE: '/message',
}
