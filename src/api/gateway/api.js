export default {
  /* Account */
  LOGIN: ['POST', 'https://192.168.6.3:8073/auth/signin'],
  TOKEN: ['POST', '/auth/oauth/token'],
  ACCOUNT: ['GET', '/users/info', { type: 'form' }],
  USER_INFO: ['GET', '/users/{userId}'],

  /* LICENSE */
  GET_LICENSE: ['GET', '/licenses/plan/{userId}'],

  /* RECORD */
  RECORD_FILES: ['GET', 'https://192.168.6.3:8073/remote/recorder/file'],
  DOWNLOAD_RECORD_FILE: [
    'GET',
    'https://192.168.6.3:8073/remote/recorder/file/download/{id}',
    { type: 'application/octet-stream', responseType: 'blob' },
  ],
  DELETE_RECORD_FILE: [
    'DELETE',
    'https://192.168.6.3:8073/remote/recorder/file/{id}',
  ],

  /* USER */
  USER_LIST: ['GET'],
}
