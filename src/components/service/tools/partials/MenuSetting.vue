<template>
  <div>
    <menu-button
      :text="$t('service.record_local_setting')"
      :active="status"
      :disabled="!canRecord"
      :src="require('assets/image/ic_setting.svg')"
      @click="setting"
    ></menu-button>
    <setting-modal
      :visible.sync="status"
      :recording="recording"
      :viewType="viewType"
    ></setting-modal>
  </div>
</template>

<script>
import toolMixin from './toolMixin'
import toastMixin from 'mixins/toast'
import SettingModal from '../../modal/SettingModal'
import { mapGetters } from 'vuex'
import { ROLE } from 'configs/remote.config'
export default {
  name: 'SettingMenu',
  components: {
    SettingModal,
  },
  mixins: [toolMixin, toastMixin],
  data() {
    return {
      status: false,
      recording: false,
    }
  },
  props: {
    viewType: String,
  },
  computed: {
    ...mapGetters(['control']),
    canRecord() {
      if (this.disabled) {
        return false
      }
      if (this.account.roleType === ROLE.LEADER) {
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
    setting() {
      if (!this.canRecord) {
        // TODO: MESSAGE
        this.toastDefault(this.$t('service.record_blocked'))
        return
      }
      this.status = !this.status
    },
    localRecording(isStart) {
      this.recording = isStart
    },
  },

  /* Lifecycles */
  beforeDestroy() {
    this.$eventBus.$off('localRecord')
  },
  mounted() {
    this.$eventBus.$on('localRecord', this.localRecording)
  },
}
</script>
