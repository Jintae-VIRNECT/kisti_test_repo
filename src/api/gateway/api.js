export default {
  /* Account */
  LOGIN: ['POST', 'https://192.168.6.3:8073/auth/signin'],
  TOKEN: ['POST', 'https://192.168.6.3:8073/auth/oauth/token'],
  ACCOUNT: ['GET', 'https://192.168.6.3:8073/users/info', { type: 'form' }],
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
  // ROOM_LIST: ['GET', '/media/room?paging={paging}'],
  // ROOM_INFO: ['GET', '/media/room/{roomId}'],
  UPDATE_ROOM_INFO: ['PUT', '/media/room/{roomId}', { type: 'form' }],
  LEAVE_ROOM: ['DELETE', '/media/room/{roomId}/participants/{participantsId}'],
  PARTICIPANTS_LIST: ['GET', '/media/room/{roomId}/participants'],
  INVITE_PARTICIPANTS_LIST: ['GET', '/media/room/participants'],
  // CREATE_ROOM: [
  //   'POST',
  //   '/media/room?workspaceId={workspaceId}',
  //   { type: 'form' },
  // ],
  DELETE_ROOM: ['DELETE', '/media/room/{roomId}'],

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

  /* CALL */
  GET_TOKEN: ['POST', '/media/tokens'],

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
