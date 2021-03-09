<template>
  <div ref="pano-video" class="pano-container"></div>
</template>

<script>
import { mapGetters, mapMutations, mapActions } from 'vuex'

import { PanoViewer } from '@egjs/view360'
import { ROLE } from 'configs/remote.config'

export default {
  name: 'PanoVideo',
  props: {
    videoElementId: {
      type: String,
    },
    targetRef: {
      type: String,
      required: true,
    },
    connectionId: {
      type: String,
      // required: true,
    },
    type: {
      type: String,
      validator: type => {
        return ['sub', 'control', 'viewer'].indexOf(type) !== -1
      },
    },
  },
  data() {
    return {
      panoViewer: null,
      defaultFov: 85, //fix
      videoElement: null,
    }
  },
  computed: {
    ...mapGetters(['myInfo', 'mainView']),
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
  },
  methods: {
    ...mapMutations(['updateParticipant']),
    ...mapActions(['setMainPanoCanvas']),
    rotate(info) {
      // console.log('panovideo::', info)
      // connectionId: connectionId,
      // yaw: data.yaw,
      // pitch: data.pitch,

      //내가 컨트롤 하고있는 메인뷰의 pano view는 돌면 안됨
      if (this.type === 'control') {
        return
      }

      //도세요
      this.panoViewer.lookAt({
        yaw: info.yaw,
        pitch: info.pitch,
        fov: this.defaultFov,
      })
    },
    initPano() {
      console.log('current pano viewer type::', this.type)
      //동적으로 캔버스 크기 제어 필요함

      this.isPanoView = true

      const container = this.$refs['pano-video']

      if (this.$parent.$refs[this.targetRef]) {
        this.videoElement = this.$parent.$refs[this.targetRef]
      } else if (this.$parent.children) {
        this.videoElement = this.$parent.children.find(node => {
          return node.tag === 'video'
        }).elm
      } else {
        this.videoElement = document.querySelector('#' + this.videoElementId)
      }

      container.style.width = this.videoElement.offsetWidth + 'px'
      container.style.height = this.videoElement.offsetHeight + 'px'

      this.videoElement.style.visibility = 'hidden'

      if (!this.panoViewer) {
        this.panoViewer = new PanoViewer(container, {
          video: this.videoElement,
          projectionType: PanoViewer.PROJECTION_TYPE.EQUIRECTANGULAR,
          stereoFormat: '',
          useZoom: false,
          touchDirection: PanoViewer.TOUCH_DIRECTION.NONE,
        })

        this.panoViewer.on('viewChange', e => {
          if (this.type === 'control') {
            console.log('그냥 yaw', e.yaw)
            console.log('그냥 pitch', e.pitch)

            this.$call.sendPanoRotation({
              yaw: e.yaw,
              pitch: e.pitch,
              origin: this.mainView.connectionId,
            })

            this.updateParticipant({
              connectionId: this.mainView.connectionId,
              rotationPos: { yaw: e.yaw, pitch: e.pitch },
            })
          }
        })

        this.panoViewer.on('ready', () => {
          if (this.type === 'viewer') {
            this.setMainPanoCanvas(this.$el.getElementsByTagName('canvas')[0])
          }
          if (this.mainView.rotationPos) {
            const yaw = Number.parseFloat(this.mainView.rotationPos.yaw)
            const pitch = Number.parseFloat(this.mainView.rotationPos.pitch)
            this.panoViewer.lookAt({
              yaw: yaw,
              pitch: pitch,
              fov: this.defaultFov,
            })
          }
        })
      }
    },
    toggle(flag) {
      if (this.panoViewer && this.type === 'control') {
        if (flag) {
          console.log('active pano main viewer')
          this.panoViewer.setTouchDirection(PanoViewer.TOUCH_DIRECTION.ALL)
        } else {
          console.log('deactive pano main viewer')
          this.panoViewer.setTouchDirection(PanoViewer.TOUCH_DIRECTION.NONE)
        }
      }
    },
    resize() {
      if (this.panoViewer) {
        this.panoViewer.updateViewportDimensions()
      }
    },
  },
  mounted() {
    this.$eventBus.$on('panoview:rotation', this.rotate)
    this.$eventBus.$on('panoview:toggle', this.toggle)

    this.initPano()

    window.addEventListener('resize', this.resize)
  },
  beforeDestroy() {
    this.setMainPanoCanvas(null)

    this.$eventBus.$off('panoview:rotation', this.rotate)
    this.$eventBus.$off('panoview:toggle', this.toggle)

    window.removeEventListener('resize', this.resize)
    this.videoElement.style.visibility = 'visible'
  },
}
</script>

<style lang="scss" scoped>
.pano-container {
  outline: none;
}
</style>
