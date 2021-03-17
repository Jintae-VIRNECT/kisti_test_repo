<template>
  <div ref="pano-video" class="pano-container" :data-type="type"></div>
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
    activePano: {
      type: Boolean,
      default: false,
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
    ...mapGetters(['myInfo', 'mainView', 'viewForce']),
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
  },
  watch: {
    activePano: {
      handler(flag) {
        this.toggle(flag)
      },
      immediate: true,
    },
  },
  methods: {
    ...mapMutations(['updateParticipant']),
    ...mapActions(['setMainPanoCanvas']),
    rotate(info) {
      if (this.type === 'control') {
        return
      }
      if (this.viewForce) {
        this.panoViewer.lookAt({
          yaw: info.yaw,
          pitch: info.pitch,
          fov: this.defaultFov,
        })
      }
    },
    initPano() {
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

      container.style.width = '100%'
      container.style.height = '100%'

      if (this.type !== 'control') {
        this.videoElement.style.visibility = 'hidden'
      }

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
          this.toggle(this.activePano)
        })
      }
    },
    toggle(flag) {
      if (this.panoViewer && this.type === 'control') {
        if (flag) {
          this.panoViewer.setTouchDirection(PanoViewer.TOUCH_DIRECTION.ALL)
        } else {
          this.panoViewer.setTouchDirection(PanoViewer.TOUCH_DIRECTION.NONE)
        }
      }
    },
    resize() {
      if (this.panoViewer) {
        setTimeout(() => {
          this.panoViewer.updateViewportDimensions()
        }, 1000)
      }
    },
  },
  mounted() {
    this.$eventBus.$on('panoview:rotation', this.rotate)
    this.$eventBus.$on('video:fullscreen', this.resize)

    this.initPano()

    window.addEventListener('resize', this.resize)
  },
  beforeDestroy() {
    if (this.type === 'viewer') {
      this.setMainPanoCanvas(null)
    }

    this.$eventBus.$off('panoview:rotation', this.rotate)
    this.$eventBus.$off('video:fullscreen', this.resize)

    window.removeEventListener('resize', this.resize)
    if (this.type !== 'control') {
      this.videoElement.style.visibility = 'visible'
    }
  },
}
</script>

<style lang="scss">
.pano-container {
  outline: none;
}
</style>
