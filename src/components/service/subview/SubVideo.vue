<template>
  <div class="sub-video">
    <transition name="opacity">
      <video
        class="sub-video--screen"
        v-if="stream !== null"
        ref="subVideo"
        autoplay
        loop
        muted
        :srcObject.prop="stream"
      ></video>
      <div v-else class="sub-video--no-stream"></div>
    </transition>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { CAMERA } from 'configs/device.config'
import { VIEW, ACTION } from 'configs/view.config'
export default {
  name: 'SubVideo',
  components: {},
  data() {
    return {}
  },
  props: {},
  computed: {
    ...mapGetters(['mainView', 'deviceInfo', 'view', 'viewAction']),
    stream() {
      if (this.mainView && this.mainView.id) {
        if (this.view === VIEW.AR && this.viewAction !== ACTION.AR_DRAWING) {
          return null
        } else {
          if (
            this.deviceInfo.cameraStatus === CAMERA.CAMERA_ON ||
            this.deviceInfo.cameraStatus === 'default'
          ) {
            return this.mainView.stream
          } else {
            return null
          }
        }
      } else {
        return null
      }
    },
  },
}
</script>
