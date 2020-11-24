<template>
  <menu-button
    :text="$t('service.record_server')"
    :active="isRecording"
    :disabled="disabled"
    :src="require('assets/image/call/ic_record_off.svg')"
    :isActive="isRecording"
    :activeSrc="require('assets/image/call/ic_record_ing.svg')"
    @click="recording"
    :isWaiting="isWaiting"
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
      isWaiting: false,
    }
  },
  methods: {
    recording() {
      if (this.disabled) return false
      if (this.isWaiting) return false

      if (this.isRecording) {
        this.stop()
      } else {
        this.start()
      }
    },
    start() {
      this.$eventBus.$emit('serverRecord', {
        isStart: true,
        isWaiting: true,
      })
    },
    stop() {
      this.$eventBus.$emit('serverRecord', {
        isStart: false,
      })
    },
    toggleButton(payload) {
      this.isRecording = payload.isStart
      this.isWaiting = payload.isWaiting
    },
  },

  /* Lifecycles */
  mounted() {
    this.$eventBus.$on('serverRecord', this.toggleButton)
  },
  beforeDestroy() {
    this.$eventBus.$off('serverRecord')
  },
}
</script>
