export default {
  /* Account */
  LOGIN: ['POST', 'https://192.168.6.3:8073/auth/signin'],
  TOKEN: ['POST', '/auth/oauth/token'],
  ACCOUNT: ['GET', '/users/info', { type: 'form' }],
  // ACCESS_TOKEN: ['POST', '/auth/accessToken'],

  /* Workspace - History */
  GET_HISTORY_LIST: ['GET', 'https://192.168.6.4:4443/media/history'],
  GET_HISTORY_ITEM: ['GET', 'https://192.168.6.4:4443/media/history/{roomId}'],
  DELETE_HISTORY_ITEM: [
    'PUT',
    'https://192.168.6.4:4443/media/history/{roomId}',
  ],
  DELETE_HISTORY_ALL: ['DELETE', 'https://192.168.6.4:4443/media/history'],

  /* Workspace - Member */
  GET_MEMBER_LIST: ['GET', '/workspaces/{workspaceId}/members?size={size}'],
  // GET_MEMBER_LIST: ['GET', 'https://192.168.6.4:4443/media/member/'],

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
  GET_LICENSE: ['GET', '/licenses/{workspaceId}/{userId}'],
  /* MESSAGE */
  SEND_PUSH: ['POST', '/messages/push'],
}
