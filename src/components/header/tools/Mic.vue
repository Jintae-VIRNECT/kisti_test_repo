<template>
  <tooltip :content="`${$t('common.mic')} on/off`">
    <toggle-button
      slot="body"
      customClass="toggle-header"
      :description="`${$t('common.mic')} on/off`"
      size="2.429rem"
      :active="mic.isOn"
      :activeSrc="require('assets/image/call/gnb_ic_voice_on.svg')"
      :inactiveSrc="require('assets/image/call/gnb_ic_voice_off.svg')"
      @action="micOnOff"
    ></toggle-button>
  </tooltip>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'

import Tooltip from 'Tooltip'
import ToggleButton from 'ToggleButton'
export default {
  name: 'Mic',
  components: {
    Tooltip,
    ToggleButton,
  },
  computed: {
    ...mapGetters(['mic']),
  },
  methods: {
    ...mapActions(['setDevices']),
    micOnOff() {
      let mic = !this.mic.isOn

      this.setDevices({
        mic: {
          isOn: mic,
        },
      })
      this.$call.mic(mic)
      this.$localStorage.setDevice('mic', 'isOn', mic)
    },
  },
}
</script>
