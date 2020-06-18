<template>
  <div class="sub-video">
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
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
  name: 'SubVideo',
  components: {},
  data() {
    return {
      stream: null,
    }
  },
  props: {},
  computed: {
    ...mapGetters(['mainView']),
  },
  watch: {
    mainView: {
      handler(mainView) {
        if (mainView !== undefined && mainView.hasOwnProperty('stream')) {
          this.stream = mainView.stream
        } else {
          this.stream = null
        }
      },
    },
    deep: true,
  },
}
</script>
