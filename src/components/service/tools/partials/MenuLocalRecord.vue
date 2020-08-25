<template>
  <menu-button
    :text="$t('service.record_local')"
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
  },
  watch: {},

  methods: {
    ...mapActions(['setLocalRecordStatus']),
    async recording() {
      if (this.disabled) return false

      if (!this.canRecord) {
        // TODO: MESSAGE
        this.toastDefault(this.$t('service.record_blocked'))
        return false
      }

      if (this.localRecordStatus === LCOAL_RECORD_STAUTS.START) {
        this.$eventBus.$emit('localRecord', false)
        return false
      } else {
        this.$eventBus.$emit('localRecord', true)
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
