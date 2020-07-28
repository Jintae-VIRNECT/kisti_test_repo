export default {
  /* Account */
  LOGIN: ['POST', 'https://192.168.6.3:8073/auth/signin'],
  TOKEN: ['POST', '/auth/oauth/token'],
  ACCOUNT: ['GET', '/users/info', { type: 'form' }],
  // ACCESS_TOKEN: ['POST', '/auth/accessToken'],

  /* Workspace - History */
  HISTORY_LIST: ['GET', '/remote/history'],
  HISTORY_ITEM: [
    'GET',
    'https://192.168.13.36:5000/remote/history/{workspaceId}/{sessionId}',
  ],
  DELETE_HISTORY_ITEM: [
    'DELETE',
    'https://192.168.13.36:5000/remote/history/{workspaceId}',
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
  ROOM_LIST: ['GET', 'https://192.168.6.4:4443/media/room?paging={paging}'],
  ROOM_INFO: ['GET', 'https://192.168.6.4:4443/media/room/{roomId}'],
  UPDATE_ROOM_INFO: [
    'PUT',
    'https://192.168.6.4:4443/media/room/{roomId}',
    { type: 'form' },
  ],
  LEAVE_ROOM: [
    'DELETE',
    'https://192.168.6.4:4443/media/room/{roomId}/participants/{participantsId}',
  ],
  PARTICIPANTS_LIST: [
    'GET',
    'https://192.168.6.4:4443/media/room/{roomId}/participants',
  ],
  INVITE_PARTICIPANTS_LIST: [
    'GET',
    'https://192.168.6.4:4443/media/room/participants',
  ],
  CREATE_ROOM: [
    'POST',
    'https://192.168.6.4:4443/media/room?workspaceId={workspaceId}',
    { type: 'form' },
  ],
  DELETE_ROOM: ['DELETE', 'https://192.168.6.4:4443/media/room/{roomId}'],

  /* CALL */
  GET_TOKEN: ['POST', 'https://192.168.6.4:4443/media/tokens'],

  /* LICENSE */
  GET_LICENSE: [
    'GET',
    'https://192.168.13.36:5000/remote/licenses/{workspaceId}/{userId}',
  ],
  /* MESSAGE */
  SEND_PUSH: ['POST', '/messages/push'],
}
