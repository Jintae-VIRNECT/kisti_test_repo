<template>
  <div class="pinchzoom-layer-container">
    <div class="pinchzoom-layer" ref="pinchzoom-layer"></div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import PinchZoom from 'pinch-zoom-js'
import { CAMERA } from 'configs/device.config'
import toastMixin from 'mixins/toast'

//Zoom.vue와 중복되는 로직들이 있지만, 디벨롭 브랜치와 컨플릭트 최소화 하기 위해 별도 믹스인으로 공통분류 하지 않았음.
//머지 후 리펙토링 필요
export default {
  mixins: [toastMixin],
  data() {
    return {
      pinchZoom: null,
    }
  },
  computed: {
    ...mapGetters(['mainView']),
    //전체 공유 기기의 현재 zoom 값
    zoomLevel() {
      if (this.mainView && this.mainView.id) {
        return this.mainView.zoomLevel
      }
      return 1
    },
    //전체 공유 기기의 zoom 최대 값
    zoomMax() {
      if (this.mainView && this.mainView.id) {
        return this.mainView.zoomMax
      }
      return 1
    },
    //전체 공유 기기의 카메라 상태
    cameraStatus() {
      if (this.mainView && this.mainView.id) {
        return this.mainView.camera
      }
      return -1
    },
  },
  watch: {
    zoomMax(newVal) {
      if (this.pinchZoom) {
        this.pinchZoom.options.maxZoom = newVal
      }
    },
    cameraStatus(level) {
      this.cameraListener(level)
    },
  },
  methods: {
    cameraListener(status) {
      // 응답
      if (parseInt(status) === CAMERA.CAMERA_ZOOMING) {
        this.toastNotice(this.$t('service.camera_zooming'))
        this.pinchZoom.enable()
        return
      }
      if (parseInt(status) === CAMERA.APP_IS_BACKGROUND) {
        this.toastDefault(this.$t('service.camera_app_disable'))
      }
      if (parseInt(status) === CAMERA.NO_PERMISSION) {
        this.toastNotice(this.$t('service.camera_no_permission'))
      }
      this.pinchZoom.disable()
    },

    change(val) {
      if (val >= this.zoomMax) val = 4.0
      if (val <= 1) val = 1.0

      let value = val.toFixed(1)

      if (value < 1) {
        value = 1
      }

      if (value > this.zoomMax) {
        value = this.zoomMax
      }

      this.changeZoomLevel(value)
    },

    changeZoomLevel(level) {
      if (this.cameraStatus === -1) {
        this.toastDefault(this.$t('service.camera_permission'))
        return
      }
      this.$emit('zoomLevelChanged', level)
      this.$call.sendCameraZoom(level, [this.mainView.connectionId])
    },
  },
  created() {
    this.$nextTick(() => {
      const el = this.$refs['pinchzoom-layer']
      this.pinchZoom = new PinchZoom(el, {
        minZoom: 1,
        maxZoom: this.zoomMax,
        onZoomUpdate: (object, event) => {
          if (event) event.preventDefault()
          this.change(object.zoomFactor)
        },
      })
    })
  },
}
</script>

<style lang="scss">
.pinchzoom-layer-container {
  position: absolute;
  z-index: 1;
  width: 100%;
  height: 100%;
}
.pinch-zoom-container {
  position: absolute !important;
  z-index: 1;
  width: 100% !important;
  height: 100% !important;
  > .pinchzoom-layer {
    width: 100%;
    height: 100%;
  }
}
</style>
