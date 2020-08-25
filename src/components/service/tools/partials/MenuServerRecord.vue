<template>
  <menu-button
    :text="$t('service.record_server')"
    :active="isRecording"
    :disabled="disabled"
    :src="require('assets/image/ic_record_off.svg')"
    :icActive="isRecording"
    :activeSrc="require('assets/image/ic_record_ing.svg')"
    @click="recording"
  ></menu-button>
</template>

<script>
import toolMixin from './toolMixin'
export default {
  name: 'ServerRecordMenu',
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
      if (this.disabled) return
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
