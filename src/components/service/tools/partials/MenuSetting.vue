<template>
  <div>
    <menu-button
      text="로컬 녹화 설정"
      :active="status"
      :disabled="!canRecord"
      :src="require('assets/image/ic_setting.svg')"
      @click="setting"
    ></menu-button>
    <record-setting
      :visible.sync="status"
      :recording="recording"
    ></record-setting>
  </div>
</template>

<script>
import toolMixin from './toolMixin'
import toastMixin from 'mixins/toast'
import RecordSetting from '../../modal/ServiceLocalRecordSetting'
import { mapGetters } from 'vuex'
import { ROLE } from 'configs/remote.config'
export default {
  name: 'SettingMenu',
  components: {
    RecordSetting,
  },
  mixins: [toolMixin, toastMixin],
  data() {
    return {
      status: false,
      recording: false,
    }
  },
  computed: {
    ...mapGetters(['control']),
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
    setting() {
      if (!this.canRecord) {
        // TODO: MESSAGE
        this.toastDefault('리더가 로컬 녹화를 막았습니다. >> 문구정의 필요')
        return
      }
      this.status = !this.status
      this.$eventBus.$emit('lcRecSet:show')
    },
    blockSetting(isStart) {
      if (isStart) {
        this.recording = true
      } else {
        this.recording = false
      }
    },
  },

  /* Lifecycles */
  beforeDestroy() {
    this.$eventBus.$off('localRecord')
    this.$eventBus.$off('serverRecord')
  },
  mounted() {
    this.$eventBus.$on('localRecord', this.blockSetting)
    this.$eventBus.$on('serverRecord', this.blockSetting)
  },
}
</script>
