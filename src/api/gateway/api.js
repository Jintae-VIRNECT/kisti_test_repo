export default {
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
