module.exports = {
  /**
   * User
   */
  GET_AUTH_INFO: ['GET', '/users/info'],
  /**
   * Download
   */
  APP_LIST: ['GET', '/download/list/{productName}'],
  DOWNLOAD_APP: ['GET', '/download/app/{uuid}'],
  DOWNLOAD_GUIDE: ['GET', '/download/guide/{uuid}'],
}
