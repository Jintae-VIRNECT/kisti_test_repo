export default {
  /* Account */
  LOGIN: ['POST', 'https://192.168.6.3:8073/auth/signin'],
  TOKEN: ['POST', 'https://192.168.6.3:8073/auth/oauth/token'],
  ACCOUNT: ['GET', 'https://192.168.6.3:8073/users/info', { type: 'form' }],
  // ACCESS_TOKEN: ['POST', '/auth/accessToken'],

  /* Workspace - History */
  HISTORY_LIST: ['GET', 'https://192.168.13.36:5000/remote/history'],
  HISTORY_ITEM: [
    'GET',
    'https://192.168.13.36:5000/remote/history/{workspaceId}/{sessionId}',
  ],
  DELETE_HISTORY_ITEM: [
    'DELETE',
    'https://192.168.13.36:5000/remote/history/{workspaceId}/{sessionId}/{userId}',
  ],
  DELETE_HISTORY_ALL: [
    'DELETE',
    'https://192.168.13.36:5000/remote/history/{workspaceId}/{userId}',
  ],

  /* Workspace - Member */
  MEMBER_LIST: [
    'GET',
    'https://192.168.13.36:5000/remote/members/{workspaceId}',
  ],

  /* Workspace - Room */
  ROOM_LIST: ['GET', '/media/room?paging={paging}'],
  ROOM_INFO: ['GET', '/media/room/{roomId}'],
  UPDATE_ROOM_INFO: ['PUT', '/media/room/{roomId}', { type: 'form' }],
  LEAVE_ROOM: ['DELETE', '/media/room/{roomId}/participants/{participantsId}'],
  PARTICIPANTS_LIST: ['GET', '/media/room/{roomId}/participants'],
  INVITE_PARTICIPANTS_LIST: ['GET', '/media/room/participants'],
  CREATE_ROOM: [
    'POST',
    '/media/room?workspaceId={workspaceId}',
    { type: 'form' },
  ],
  DELETE_ROOM: ['DELETE', '/media/room/{roomId}'],

  /* CALL */
  GET_TOKEN: ['POST', '/media/tokens'],

  /* LICENSE */
  GET_LICENSE: [
    'GET',
    'https://192.168.13.36:5000/remote/licenses/{workspaceId}/{userId}',
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
