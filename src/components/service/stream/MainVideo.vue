<template>
  <div class="main-video">
    <div class="main-video__box">
      <template v-if="mainView && mainView.stream">
        <video
          ref="mainVideo"
          id="main-video"
          :srcObject.prop="mainView.stream"
          @resize="optimizeVideoSize"
          @loadeddata="optimizeVideoSize"
          :muted="!speaker"
          autoplay
          playsinline
          loop
        ></video>
      </template>

      <pointing v-if="true" :scale="1" class="main-video__pointing"></pointing>
      <!-- 
      <div class="main-video__info">
        <img class="profile" src="~assets/image/call/chat_img_user.svg" />
        <span class="name">{{ mainView.nickName }}</span>
        <span class="status" :class="status">연결상태</span>
      </div> -->
      <!-- 
      <button v-if="mainView.uuid === 'main'" class="main-video__setting">
        화면 설정
      </button> -->
    </div>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'

import Pointing from './StreamPointing'
export default {
  name: 'MainVideo',
  components: {
    Pointing,
  },
  data() {
    return {
      status: 'good', // good, normal, bad
    }
  },
  computed: {
    ...mapGetters({
      mainView: 'mainView',
      speaker: 'speaker',
    }),
  },
  watch: {
    speaker(val) {
      this.$refs['mainVideo'].muted = val ? false : true
      console.log(this.$refs['mainVideo'].muted)
    },
    mainView: {
      deep: true,
      handler(e) {
        console.log(e)
      },
    },
  },
  methods: {
    ...mapActions(['updateAccount']),
    optimizeVideoSize() {
      const mainWrapper = this.$el
      const videoBox = this.$el.querySelector('.main-video__box')
      const videoEl = this.$el.querySelector('#main-video')

      let videoWidth = videoEl.offsetWidth,
        videoHeight = videoEl.offsetHeight,
        wrapperWidth = mainWrapper.offsetWidth,
        wrapperHeight = mainWrapper.offsetHeight

      if (videoHeight / videoWidth > wrapperHeight / wrapperWidth) {
        videoBox.style.width = 'auto'
        videoEl.style.width = 'auto'
        videoEl.style.height = '100%'
        videoWidth = videoEl.offsetWidth
        videoBox.style.width = videoWidth + 'px'
      } else {
        videoBox.style.height = 'auto'
        videoEl.style.width = '100%'
        videoEl.style.height = 'auto'
        videoHeight = videoEl.offsetHeight
        videoBox.style.height = videoHeight + 'px'
      }
    },
    capture() {
      const videoEl = this.$el.querySelector('#main-video')

      const width = videoEl.offsetWidth
      const height = videoEl.offsetHeight

      const tmpCanvas = document.createElement('canvas')
      tmpCanvas.width = width
      tmpCanvas.height = height

      const tmpCtx = tmpCanvas.getContext('2d')
      tmpCtx.drawImage(videoEl, 0, 0, width, height)
      tmpCanvas.toBlob(blob => {
        const a = document.createElement('a')
        document.body.appendChild(a)
        const url = window.URL.createObjectURL(blob)
        a.href = url
        a.download = '캡쳐.png'
        a.click()
        setTimeout(() => {
          window.URL.revokeObjectURL(url)
          document.body.removeChild(a)
        }, 0)
        // this.imgBlob = blob
      }, 'image/png')
    },
  },

  /* Lifecycles */
  beforeDestroy() {
    // this.$call.leave()
    this.$eventBus.$off('capture', this.capture)
    window.removeEventListener('resize', this.optimizeVideoSize)
  },
  created() {
    this.$eventBus.$on('capture', this.capture)
    window.addEventListener('resize', this.optimizeVideoSize)
  },
  mounted() {},
}
</script>
