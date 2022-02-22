<template>
  <div class="main-video__empty-inner stopped">
    <img :src="videoStopSrc" />
    <p>{{ $t('service.stream_stop') }}</p>
    <p
      class="inner-discription"
      v-if="
        cameraStatus !== CAMERA_STATE.UNAVAILABLE &&
          cameraStatus.state === CAMERA_STATE.BACKGROUND
      "
      v-html="$t('service.stream_background')"
    ></p>
    <p class="inner-discription" v-else>
      {{ $t('service.stream_stoped') }}
    </p>
  </div>
</template>

<script>
import { CAMERA_STATE } from 'configs/status.config'

export default {
  name: 'VideoStopped',
  props: {
    cameraStatus: {
      type: [Object, Number],
    },
  },
  data() {
    return {
      CAMERA_STATE: Object.freeze(CAMERA_STATE),
    }
  },
  computed: {
    videoStopSrc() {
      return this.isMobileSize
        ? require('assets/image/img_video_stop_new.svg')
        : require('assets/image/img_video_stop.svg')
    },
  },
}
</script>

<style></style>
