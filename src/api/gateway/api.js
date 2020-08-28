export default {
  /* Account */
  LOGIN: ['POST', 'https://192.168.6.3:8073/auth/signin'],
  TOKEN: ['POST', '/auth/oauth/token'],
  ACCOUNT: ['GET', '/users/info', { type: 'form' }],
  USER_INFO: ['GET', '/users/{userId}'],
  /* Workspace - History */
  HISTORY_LIST: [
    'GET',
    'https://192.168.13.36:8000/remote/history?userId={userId}',
  ],
  HISTORY_ITEM: [
    'GET',
    'https://192.168.13.36:8000/remote/history/{workspaceId}/{sessionId}',
  ],
  DELETE_HISTORY_ITEM: [
    'DELETE',
    'https://192.168.13.36:8000/remote/history/{workspaceId}',
  ],
  DELETE_HISTORY_ALL: [
    'DELETE',
    'https://192.168.13.36:8000/remote/history/{workspaceId}/{userId}',
  ],

  /* Workspace - Member */
  MEMBER_LIST: [
    'GET',
    'https://192.168.13.36:8000/remote/members/{workspaceId}/{userId}',
  ],
  INVITABLE_MEMBER_LIST: [
    'GET',
    'https://192.168.13.36:8000/remote/members/{workspaceId}/{sessionId}/{userId}',
  ],

  /* Workspace - Room */
  ROOM_LIST: [
    'GET',
    'https://192.168.13.36:8000/remote/room?workspaceId={workspaceId}',
  ],
  CREATE_ROOM: ['POST', 'https://192.168.13.36:8000/remote/room'],
  UPDATE_ROOM_PROFILE: [
    'POST',
    'https://192.168.13.36:8000/remote/file/{workspaceId}/{sessionId}/profile',
    { type: 'form' },
  ],
  JOIN_ROOM: [
    'POST',
    'https://192.168.13.36:8000/remote/room/{workspaceId}/{sessionId}/join',
  ],
  ROOM_INFO: [
    'GET',
    'https://192.168.13.36:8000/remote/room/{workspaceId}/{sessionId}',
  ],
  DELETE_ROOM: [
    'DELETE',
    'https://192.168.13.36:8000/remote/room/{workspaceId}/{sessionId}/{userId}',
  ],
  LEAVE_ROOM: [
    'DELETE',
    'https://192.168.13.36:8000/remote/room/{workspaceId}/{sessionId}/exit?userId={userId}',
  ],
  UPDATE_ROOM_INFO: [
    'POST',
    'https://192.168.13.36:8000/remote/room/{workspaceId}/{sessionId}/info',
  ],

  /* CALL */
  INVITE_ROOM: [
    'POST',
    'https://192.168.13.36:8000/remote/room/{workspaceId}/{sessionId}/member',
  ],
  KICKOUT_MEMBER: [
    'DELETE',
    'https://192.168.13.36:8000/remote/room/{workspaceId}/{sessionId}/member',
  ],
  SEND_SIGNAL: [
    'POST',
    'https://192.168.13.36:8000/remote/room/{workspaceId}/signal',
  ],

  /* LICENSE */
  GET_LICENSE: ['GET', '/licenses/plan/{userId}'],
  /* MESSAGE */
  SEND_PUSH: ['POST', 'https://192.168.13.36:8000/remote/message/push'],

  /* CHAT FILE */
  SEND_FILE: [
    'POST',
    'https://192.168.13.94:4443/file/upload',
    { type: 'form' },
  ],
  FILE_UPLOAD: [
    'POST',
    'https://192.168.13.36:8000/remote/file/upload',
    { type: 'form' },
  ],
  FILE_DOWNLOAD: [
    'GET',
    'https://192.168.13.36:8000/remote/file/download/{workspaceId}/{sessionId}',
    { response: 'direct', type: 'blob' },
  ],
  FILE_LIST: ['GET', 'https://192.168.13.36:8000/remote/file'],

  /* SERVER RECORD */
  START_SERVER_RECORD: ['POST', '/remote/recorder/recording'],
  STOP_SERVER_RECORD: ['DELETE', '/remote/recorder/recording/{id}'],
}

export const wsUri = {
  REMOTE: 'https://192.168.13.36:8000/remote/websocket',
  MESSAGE: '/message',
}
