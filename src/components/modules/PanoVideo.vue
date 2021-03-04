<template>
  <div ref="pano-video" class="pano-container"></div>
</template>

<script>
import { mapGetters } from 'vuex'

import { PanoViewer } from '@egjs/view360'
import { ROLE } from 'configs/remote.config'

export default {
  name: 'PanoVideo',
  props: {
    targetRef: {
      type: String,
      // required: true,
    },
    connectionId: {
      type: String,
      // required: true,
    },
  },
  data() {
    return {
      panoViewer: null,
      defaultFov: 85, //fix
    }
  },
  computed: {
    ...mapGetters(['myInfo']),
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
  },
  methods: {
    rotate(info) {
      console.log('panovideo::', info)
      // connectionId: connectionId,
      // yaw: data.yaw,
      // pitch: data.pitch,

      const signalFromMe = this.myInfo.connectionId === info.connectionId

      if (this.connectionId !== info.connectionId) return
      if (signalFromMe && this.targetRef === 'mainVideo') return

      //도세요
      this.panoViewer.lookAt({
        yaw: info.yaw,
        pitch: info.pitch,
        fov: this.defaultFov,
      })
    },
    initPano() {
      //동적으로 캔버스 크기 제어 필요함

      this.isPanoView = true

      const container = this.$refs['pano-video']
      let videoElement = null
      if (this.$parent.$refs[this.targetRef]) {
        videoElement = this.$parent.$refs[this.targetRef]
      } else {
        videoElement = this.$parent.children.find(node => {
          return node.tag === 'video'
        }).elm
      }

      console.log(videoElement.offsetWidth)
      console.log(videoElement.offsetHeight)
      container.style.width = videoElement.offsetWidth + 'px'
      container.style.height = videoElement.offsetHeight + 'px'

      videoElement.style.visibility = 'hidden'

      if (!this.panoViewer) {
        this.panoViewer = new PanoViewer(container, {
          video: videoElement,
          projectionType: PanoViewer.PROJECTION_TYPE.EQUIRECTANGULAR,
          stereoFormat: '',
          useZoom: false,
          touchDirection: PanoViewer.TOUCH_DIRECTION.NONE,
        })

        if (this.targetRef === 'mainVideo') {
          this.panoViewer.setTouchDirection(PanoViewer.TOUCH_DIRECTION.ALL)
        }

        //@To-do:전체 공유 체크
        this.panoViewer.on('viewChange', e => {
          if (!this.isLeader || this.targetRef !== 'mainVideo') return

          this.$call.sendLinkFlowControl({
            yaw: e.yaw.toFixed(2),
            pitch: e.pitch.toFixed(2),
          })
        })
      }
    },
  },
  mounted() {
    console.log('this.targetRef::', this.targetRef)
    console.log('this.$parent::', this.$parent)
    this.$eventBus.$on('linkflow:rotation', this.rotate)

    this.initPano()
  },
  beforeDestroy() {
    this.$eventBus.$off('linkflow:rotation', this.rotate)
  },
}
</script>

<style lang="scss" scoped>
.pano-container {
  outline: none;
}
</style>
