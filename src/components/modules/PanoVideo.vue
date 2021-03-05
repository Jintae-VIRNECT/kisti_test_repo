<template>
  <div ref="pano-video" class="pano-container"></div>
</template>

<script>
import { mapGetters, mapMutations } from 'vuex'

import { PanoViewer } from '@egjs/view360'
import { ROLE } from 'configs/remote.config'

import _ from 'lodash'

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
      let videoElement = null

      if (this.$parent.$refs[this.targetRef]) {
        videoElement = this.$parent.$refs[this.targetRef]
      } else if (this.$parent.children) {
        videoElement = this.$parent.children.find(node => {
          return node.tag === 'video'
        }).elm
      } else {
        videoElement = document.querySelector('#' + this.videoElementId)
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

        // if (this.type === 'main') {
        //   this.panoViewer.setTouchDirection(PanoViewer.TOUCH_DIRECTION.ALL)
        // }

        //@TODO:전체 공유 체크
        // const updateFunc = e => {
        //   if (this.type === 'control') {
        //     this.$call.sendLinkFlowControl({
        //       yaw: e.yaw.toFixed(2),
        //       pitch: e.pitch.toFixed(2),
        //     })

        //     this.updateParticipant({
        //       connectionId: this.mainView.connectionId,
        //       rotationPos: { yaw: e.yaw.toFixed(2), pitch: e.pitch.toFixed(2) },
        //     })
        //   }
        // }

        // this.panoViewer.on('viewChange', _.debounce(updateFunc, 50))
        this.panoViewer.on('viewChange', e => {
          if (this.type === 'control') {
            this.$call.sendLinkFlowControl({
              yaw: e.yaw.toFixed(2),
              pitch: e.pitch.toFixed(2),
            })

            this.updateParticipant({
              connectionId: this.mainView.connectionId,
              rotationPos: { yaw: e.yaw.toFixed(2), pitch: e.pitch.toFixed(2) },
            })
          }
        })

        this.panoViewer.on('ready', () => {
          if (this.mainView.rotationPos && this.type !== 'control') {
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
    console.log('this.targetRef::', this.targetRef)
    console.log('this.$parent::', this.$parent)
    this.$eventBus.$on('panoview:rotation', this.rotate)
    this.$eventBus.$on('panoview:toggle', this.toggle)

    this.initPano()

    window.addEventListener('resize', this.resize)
  },
  beforeDestroy() {
    this.$eventBus.$off('panoview:rotation', this.rotate)
    this.$eventBus.$off('panoview:toggle', this.toggle)

    window.removeEventListener('resize', this.resize)
  },
}
</script>

<style lang="scss" scoped>
.pano-container {
  outline: none;
}
</style>
