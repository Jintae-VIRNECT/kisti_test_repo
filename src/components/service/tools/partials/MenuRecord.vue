<template>
  <menu-button
    text="녹화"
    :active="isRecording"
    :disabled="disabled"
    :src="require('assets/image/ic_record_off.svg')"
    :onActive="isRecording"
    :activeSrc="require('assets/image/ic_record_ing.svg')"
    @click="recording"
  ></menu-button>
</template>

<script>
import toolMixin from './toolMixin'
export default {
  name: 'RecordMenu',
  mixins: [toolMixin],
  data() {
    return {
      isRecording: false,
    }
  },
  computed: {},
  watch: {},
  methods: {
    recording() {
      // this.active = 'recording'
      if (!this.isRecording) {
        this.record()
        this.$eventBus.$emit('serverRecord', true)
      } else {
        this.stop()
        this.$eventBus.$emit('serverRecord', false)
      }
    },
    record() {
      this.isRecording = true
    },
    stop() {
      this.isRecording = false
    },
  },

  /* Lifecycles */
  beforeDestroy() {},
  mounted() {},
}
</script>
