export default {
  /* Account */
  LOGIN: ['POST', '/auth/signin'],
  TOKEN: ['POST', '/auth/oauth/token'],
  ACCOUNT: ['GET', '3/users/info', { type: 'form' }],
  // ACCESS_TOKEN: ['POST', '/auth/accessToken'],

  /* Workspace - History */
  GET_HISTORY_LIST: ['GET', window.urls.media + '/media/history'],
  GET_HISTORY_ITEM: ['GET', window.urls.media + '/media/history/{roomId}'],
  DELETE_HISTORY_ITEM: ['PUT', window.urls.media + '/media/history/{roomId}'],
  DELETE_HISTORY_ALL: ['DELETE', window.urls.media + '/media/history'],

  /* Workspace - Member */
  GET_MEMBER_LIST: ['GET', '/workspaces/{workspaceId}/members?size={size}'],
  // GET_MEMBER_LIST: ['GET', '/media/member/'],

  /* Workspace - Room */
  ROOM_LIST: ['GET', window.urls.media + '/media/room?paging={paging}'],
  ROOM_INFO: ['GET', window.urls.media + '/media/room/{roomId}'],
  UPDATE_ROOM_INFO: [
    'PUT',
    window.urls.media + '/media/room/{roomId}',
    { type: 'form' },
  ],
  LEAVE_ROOM: [
    'DELETE',
    window.urls.media + '/media/room/{roomId}/participants/{participantsId}',
  ],
  PARTICIPANTS_LIST: [
    'GET',
    window.urls.media + '/media/room/{roomId}/participants',
  ],
  INVITE_PARTICIPANTS_LIST: [
    'GET',
    window.urls.media + '/media/room/participants',
  ],
  CREATE_ROOM: [
    'POST',
    window.urls.media + '/media/room?workspaceId={workspaceId}',
    { type: 'form' },
  ],
  DELETE_ROOM: ['DELETE', window.urls.media + '/media/room/{roomId}'],

  /* CALL */
  GET_TOKEN: ['POST', window.urls.media + '/media/tokens'],

  /* LICENSE */
  GET_LICENSE: ['GET', '/licenses/{workspaceId}/{userId}'],
  /* MESSAGE */
  SEND_PUSH: ['POST', '/messages/push'],
}
