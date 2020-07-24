export default {
  /* Account */
  LOGIN: ['POST', 'https://192.168.6.3:8073/auth/signin'],
  TOKEN: ['POST', 'https://192.168.6.3:8073/auth/oauth/token'],
  ACCOUNT: ['GET', 'https://192.168.6.3:8073/users/info', { type: 'form' }],
  USER_INFO: ['GET', 'https://192.168.6.3:8073/users/{userId}'],
  // ACCESS_TOKEN: ['POST', '/auth/accessToken'],

  /* Workspace - History */
  HISTORY_LIST: [
    'GET',
    'https://192.168.13.36:5000/remote/history?userId={userId}',
  ],
  GET_HISTORY_LIST: ['GET', '/media/history'],
  GET_HISTORY_ITEM: ['GET', '/media/history/{roomId}'],
  DELETE_HISTORY_ITEM: ['PUT', '/media/history/{roomId}'],
  DELETE_HISTORY_ALL: ['DELETE', '/media/history'],

  /* Workspace - Member */
  GET_MEMBER_LIST: [
    'GET',
    'https://192.168.6.3:8073/workspaces/{workspaceId}/members?size={size}',
  ],
  // GET_MEMBER_LIST: ['GET', '/media/member/'],

  /* Workspace - Room */
  ROOM_LIST: [
    'GET',
    'https://192.168.13.36:5000/remote/room?workspaceId={workspaceId}',
  ],
  CREATE_ROOM: ['POST', 'https://192.168.13.36:5000/remote/room'],
  JOIN_ROOM: [
    'POST',
    'https://192.168.13.36:5000/remote/room/{workspaceId}/{sessionId}/join',
  ],
  ROOM_INFO: [
    'GET',
    'https://192.168.13.36:5000/remote/room/{workspaceId}/{sessionId}',
  ],
  DELETE_ROOM: [
    'DELETE',
    'https://192.168.13.36:5000/remote/room/{workspaceId}/{sessionId}/{userId}',
  ],
  LEAVE_ROOM: [
    'DELETE',
    'https://192.168.13.36:5000/remote/room/{workspaceId}/{sessionId}/exit',
  ],
  UPDATE_ROOM_INFO: [
    'POST',
    'https://192.168.13.36:5000/remote/room/{workspaceId}/{sessionId}/info',
  ],

  /* CALL */
  GET_TOKEN: ['POST', '/media/tokens'],
  INVITE_ROOM: [
    'POST',
    'https://192.168.13.36:5000/remote/room/{workspaceId}/{sessionId}/member',
  ],
  KICKOUT_USER: [
    'DELETE',
    'https://192.168.13.36:5000/remote/room/{workspaceId}/{sessionId}/member',
  ],

  /* LICENSE */
  GET_LICENSE: [
    'GET',
    'https://192.168.6.3:8073/licenses/{workspaceId}/{userId}',
  ],
  /* MESSAGE */
  SEND_PUSH: ['POST', 'https://192.168.6.3:8073/messages/push'],

  /* CHAT FILE */
  SEND_FILE: [
    'POST',
    'https://192.168.13.94:4443/file/upload',
    { type: 'form' },
  ],
}
