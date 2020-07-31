export default {
  /* Account */
  LOGIN: ['POST', 'https://192.168.6.3:8073/auth/signin'],
  TOKEN: ['POST', '/auth/oauth/token'],
  ACCOUNT: ['GET', '/users/info', { type: 'form' }],
  USER_INFO: ['GET', '/users/{userId}'],
  /* Workspace - History */
  HISTORY_LIST: ['GET', '/remote/history?userId={userId}'],
  HISTORY_ITEM: ['GET', '/remote/history/{workspaceId}/{sessionId}'],
  DELETE_HISTORY_ITEM: ['DELETE', '/remote/history/{workspaceId}'],
  DELETE_HISTORY_ALL: ['DELETE', '/remote/history/{workspaceId}/{userId}'],

  /* Workspace - Member */
  MEMBER_LIST: ['GET', '/remote/members/{workspaceId}'],

  /* Workspace - Room */
  ROOM_LIST: ['GET', '/remote/room?workspaceId={workspaceId}'],
  CREATE_ROOM: ['POST', '/remote/room'],
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

  /* LICENSE */
  GET_LICENSE: ['GET', '/licenses/plan/{userId}'],
  /* MESSAGE */
  SEND_PUSH: ['POST', '/messages/push'],

  /* CHAT FILE */
  SEND_FILE: [
    'POST',
    'https://192.168.13.94:4443/file/upload',
    { type: 'form' },
  ],
}

export const wsUri = {
  REMOTE: '/remote/websocket',
  MESSAGE: '/message',
}
