<template>
  <menu-button
    :text="$t('service.record_server')"
    :active="isRecording"
    :disabled="!canRecord"
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

      if (!this.isRecording) {
        this.$eventBus.$emit('serverRecord', true)
      } else {
        this.$eventBus.$emit('serverRecord', false)
      }
    },
    toggleButton(isStart) {
      this.isRecording = isStart
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
