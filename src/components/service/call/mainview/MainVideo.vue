<template>
<div class="main-video">
  <div class="main-video__box">
    
    <video 
        id="main-video"
        :srcObject.prop="session.stream"
        @resize="optimizeVideoSize"
        @loadeddata="optimizeVideoSize"
        autoplay
        :muted="mute?'muted':false"
        loop>
    </video>

    <div class="main-video__info">
      <img class="profile" src="~assets/image/call/chat_img_user.png"/>
      <span class="name">{{ session.nickName }}</span>
      <span class="status" :class="status">연결상태</span>
    </div>

    <button v-if="session.nodeId === 'main'" class="main-video__setting">화면 설정</button>

  </div>
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
      session: 'mainSession',
      mute: 'mute'
    })
  },
	watch: {
    mute(val) {
      console.log(val)
    }
  },
	methods: {
    ...mapActions(['updateAccount']),
    optimizeVideoSize() {
      const mainWrapper = this.$el
      const videoBox = this.$el.querySelector('.main-video__box')
      const videoEl = this.$el.querySelector('#main-video')

      let 
        videoWidth = videoEl.offsetWidth,
        videoHeight = videoEl.offsetHeight,
        wrapperWidth = mainWrapper.offsetWidth,
        wrapperHeight = mainWrapper.offsetHeight

      if(videoHeight / videoWidth > wrapperHeight / wrapperWidth) {
        videoEl.style.width = 'auto'
        videoEl.style.height = '100%'
        videoWidth = videoEl.offsetWidth
        videoBox.style.width = videoWidth + 'px'
      } else {
        videoEl.style.width = '100%'
        videoEl.style.height = 'auto'
        videoHeight = videoEl.offsetHeight
        videoBox.style.height = videoHeight + 'px'
      }
    },
    capture() {
      const videoEl = this.$el.querySelector('#main-video')
      
      const width = videoEl.offsetWidth
      const height = videoEl.offsetHeight;

      const tmpCanvas = document.createElement('canvas');
      tmpCanvas.width = width;
      tmpCanvas.height = height;

      const tmpCtx = tmpCanvas.getContext('2d');
      tmpCtx.drawImage(videoEl, 0, 0, width, height);
      tmpCanvas.toBlob((blob)=>{
        const a = document.createElement('a');
        document.body.appendChild(a);
        const url = window.URL.createObjectURL(blob);
        a.href = url;
        a.download = '캡쳐.png';
        a.click();
        setTimeout(() => {
          window.URL.revokeObjectURL(url);
          document.body.removeChild(a);
        }, 0)
          // this.imgBlob = blob
      }, 'image/png')
    }
  },

  /* Lifecycles */
  beforeDestroy() {
    this.$openvidu.leave()
    this.$eventBus.$off('capture', this.capture)
  },
  created() {
    this.$eventBus.$on('capture', this.capture)
  },
	mounted() {
    // console.log(this.optimizeVideoSize())
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
