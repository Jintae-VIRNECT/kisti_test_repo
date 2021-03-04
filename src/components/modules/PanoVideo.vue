<template>
  <div ref="pano-video" class="pano-container"></div>
</template>

<script>
import { PanoViewer } from '@egjs/view360'

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
  methods: {
    rotate(info) {
      console.log('panovideo::', info)
      // connectionId: connectionId,
      // yaw: data.yaw,
      // pitch: data.pitch,
      if (info.connectionId === this.connectionId) {
        console.log('돌아요')
      }
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
        })
        //see you later
        // this.panoViewer.on('viewChange', e => {
        //   if (!this.isLeader) return
        //   this.$call.sendPanoView({ yaw: e.yaw, pitch: e.pitch, fov: e.fov })
        // })
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
