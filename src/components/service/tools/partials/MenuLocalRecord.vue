<template>
  <menu-button
    text="로컬 녹화"
    :active="isRecording"
    :disabled="!canRecord"
    :src="require('assets/image/ic_local_record.svg')"
    :icActive="isRecording"
    :activeSrc="require('assets/image/ic_local_record_on.svg')"
    @click="recording"
  ></menu-button>
</template>

<script>
import toolMixin from './toolMixin'
import toastMixin from 'mixins/toast'

import { mapGetters, mapActions } from 'vuex'
import { ROLE } from 'configs/remote.config'
import { LCOAL_RECORD_STAUTS } from 'utils/recordOptions'

export default {
  name: 'LocalRecordMenu',
  mixins: [toolMixin, toastMixin],
  data() {
    return {
      isRecording: false,
    }
  },

  computed: {
    ...mapGetters(['control', 'localRecordStatus']),
    canRecord() {
      if (this.disabled) {
        return false
      }
      if (this.account.roleType === ROLE.EXPERT_LEADER) {
        return true
      }
      if (this.control.localRecord) {
        return true
      } else {
        return false
      }
    },
  },
  watch: {},

  methods: {
    ...mapActions(['setLocalRecordStatus']),
    async recording() {
      if (this.disabled) return false

      if (!this.canRecord) {
        // TODO: MESSAGE
        this.toastDefault('리더가 로컬 녹화를 막았습니다.')
        return false
      }

      if (this.localRecordStatus === LCOAL_RECORD_STAUTS.START) {
        this.$eventBus.$emit('localRecord', false)
        this.setLocalRecordStatus(LCOAL_RECORD_STAUTS.STOP)
        return false
      } else {
        this.$eventBus.$emit('localRecord', true)
        this.setLocalRecordStatus(LCOAL_RECORD_STAUTS.START)
      }
    },
    toggleButton(isStart) {
      this.isRecording = isStart
    },
  },
  mounted() {
    this.$eventBus.$on('localRecord', this.toggleButton)
  },
  beforeDestroy() {
    this.$eventBus.$off('localRecord')
  },
}
</script>
