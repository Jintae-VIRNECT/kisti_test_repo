<template>
  <menu-button
    :text="$t('service.record_server')"
    :active="isRecording"
    :disabled="!canRecord"
    :src="require('assets/image/call/ic_record_off.svg')"
    :isActive="isRecording"
    :activeSrc="require('assets/image/call/ic_record_ing.svg')"
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
        this.$eventBus.$emit('serverRecord', {
          isStart: true,
        })
      } else {
        this.$eventBus.$emit('serverRecord', {
          isStart: false,
        })
      }
    },
    toggleButton(payload) {
      this.isRecording = payload.isStart
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
