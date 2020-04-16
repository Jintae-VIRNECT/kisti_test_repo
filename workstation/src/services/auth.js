export default {
  /**
   * 내 유저 아이디 가져오기
   */
  get myId() {
    const uuid = $nuxt.$store.getters['user/getUser'].uuid
    return uuid
  },
}
