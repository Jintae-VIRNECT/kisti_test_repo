<template>
  <menu-button
    :text="$t('service.record_server')"
    :active="isRecording"
    :disabled="!canRecord"
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
  computed: {
    canRecord() {
      if (this.disabled) {
        return false
      } else {
        return true
      }
    },
  },
  watch: {},
  methods: {
    recording() {
      if (this.disabled) return false
      if (this.isWaiting) return false

      if (!this.isRecording) {
        this.start()
      } else {
        this.stop()
      }
    },
    start() {
      this.isWaiting = true
      this.$eventBus.$emit('serverRecord', {
        isStart: true,
        isWaiting: this.isWaiting,
      })
    },
    stop() {
      this.$eventBus.$emit('serverRecord', {
        isStop: true,
      })
    },
    toggleButton(payload) {
      if (payload.isStop) {
        this.isRecording = false
      } else if (payload.isStart) {
        this.isRecording = true
      }

      this.isWaiting = payload.isWaiting ? true : false
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
