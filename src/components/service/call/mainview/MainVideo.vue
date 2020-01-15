<template>
<div class="main-video">
  
  <video 
      id="main-video"
      :srcObject.prop="session.stream"
      autoplay
      loop>
  </video>

  <div class="main-video__info">
    <img class="profile" src="~assets/image/call/chat_img_user.png"/>
    <span class="name">{{ session.nickName }}</span>
    <span class="status" :class="status">연결상태</span>
  </div>

  <button class="main-video__setting">화면 설정</button>
  
  <!-- <span>{{ session.nickName + '/' + session.userName }}</span> -->
</div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
export default {
	name: "MainVideo",
	components: {},
	data() {
		return {
      status: 'good'  // good, normal, bad
    }
	},
	computed: {
    ...mapGetters({
      session: 'mainSession'
    })
  },
	watch: {},
	methods: {
    ...mapActions(['updateAccount'])
  },

  /* Lifecycles */
  beforeDestroy() {
    this.$openvidu.leave()
  },
	mounted() {
    // let sessionName = "Session " + Math.floor(Math.random() * 10)
    let sessionName = "virnect"
    let nickName = "Participant " + Math.floor(Math.random() * 100)
    this.updateAccount({
      sessionName: sessionName,
      nickName: nickName
    })
    
    this.$openvidu.join(sessionName, nickName)
      .then(session => {
        console.log(session.connection)
        // session.signal({
        //   data: 'chatchat!!ㅎㅎㅎㅎㅎㅎ 채팅 메시지 잘 가능가!!!!',
        //   to:Connection[], 
        //   type: 'chat'
        // })
      })
  }
}
</script>
