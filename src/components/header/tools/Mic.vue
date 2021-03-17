<template>
  <tooltip :content="`${$t('common.mic')} on/off`">
    <toggle-button
      slot="body"
      customClass="toggle-header"
      :description="`${$t('common.mic')} on/off`"
      size="2.429rem"
      :disabled="disable"
      :active="mic.isOn"
      :activeSrc="require('assets/image/call/gnb_ic_voice_on.svg')"
      :inactiveSrc="require('assets/image/call/gnb_ic_voice_off.svg')"
      @action="micOnOff"
    ></toggle-button>
  </tooltip>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import { ROLE } from 'configs/remote.config'

import Tooltip from 'Tooltip'
import ToggleButton from 'ToggleButton'
export default {
  name: 'Mic',
  components: {
    Tooltip,
    ToggleButton,
  },
  computed: {
    ...mapGetters(['mic', 'roomInfo', 'allowCameraControl']),
    disable() {
      if (this.$route.name !== 'service') return false
      if (!this.roomInfo.audioRestrictedMode) return false
      if (this.account.roleType === ROLE.LEADER) return false
      return !this.allowCameraControl
    },
  },
  methods: {
    ...mapActions(['setDevices']),
    micOnOff() {
      if (this.disable) {
        return
      }
      let mic = !this.mic.isOn

      this.setDevices({
        mic: {
          isOn: mic,
        },
      })
      this.$call.sendMic(mic)
      window.myStorage.setDevice('mic', 'isOn', mic)
    },
  },
}
</script>
