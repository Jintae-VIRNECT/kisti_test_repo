<template>
  <menu-button
    :text="$t('service.record_local')"
    :active="isRecording"
    :disabled="!canRecord"
    :src="require('assets/image/call/ic_local_record.svg')"
    :isActive="isRecording"
    :activeSrc="require('assets/image/call/ic_local_record_on.svg')"
    @click="recording"
  ></menu-button>
</template>

<script>
import toolMixin from './toolMixin'
import toastMixin from 'mixins/toast'

import { mapGetters } from 'vuex'
import { ROLE } from 'configs/remote.config'

export default {
  name: 'LocalRecordMenu',
  mixins: [toolMixin, toastMixin],
  computed: {
    ...mapGetters(['allowLocalRecord', 'localRecordStatus']),
    canRecord() {
      if (this.disabled) {
        return false
      }
      if (this.account.roleType === ROLE.LEADER) {
        return true
      }
      if (this.allowLocalRecord) {
        return true
      } else {
        return false
      }
    },
    isRecording() {
      return this.localRecordStatus === 'START'
    },
  },

  methods: {
    async recording() {
      if (this.disabled) return false

      if (!this.canRecord) {
        this.toastDefault(this.$t('service.record_blocked'))
        return false
      }

      this.$eventBus.$emit('localRecord')
    },
  },
}
</script>
