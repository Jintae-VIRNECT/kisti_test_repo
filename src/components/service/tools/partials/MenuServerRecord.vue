<template>
  <menu-button
    :text="$t('service.record_server')"
    :active="isRecording"
    :disabled="disabled || isPreparing"
    :src="require('assets/image/call/ic_record_off.svg')"
    :isActive="isRecording"
    :activeSrc="require('assets/image/call/ic_record_ing.svg')"
    @click="recording"
    :isWaiting="isWaiting"
  ></menu-button>
</template>

<script>
import { mapGetters } from 'vuex'
import toolMixin from './toolMixin'
export default {
  name: 'ServerRecordMenu',
  mixins: [toolMixin],
  computed: {
    ...mapGetters(['serverRecordStatus']),
    isWaiting() {
      return this.serverRecordStatus === 'WAIT'
    },
    isRecording() {
      const isNotStopped = this.serverRecordStatus !== 'STOP'
      const isNotDisabled = !this.disabled
      const isNotChecking = !this.isPreparing

      return isNotStopped && isNotDisabled && isNotChecking
    },
    isPreparing() {
      return this.serverRecordStatus === 'PREPARE'
    },
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
      this.$eventBus.$emit('serverRecord', 'WAIT')
    },
    stop() {
      this.$eventBus.$emit('serverRecord', 'STOP')
    },
  },
}
</script>
