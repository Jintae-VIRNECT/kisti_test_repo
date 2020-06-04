<template>
  <tooltip content="마이크 on/off">
    <toggle-button
      slot="body"
      description="마이크 on/off"
      :size="34"
      :active="mic"
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
    ...mapActions(['callMic']),
    micOnOff() {
      let mic = !this.mic
      this.callMic(mic)

      this.$call.micOnOff(mic)
    },
  },

  /* Lifecycles */
  created() {
    let mic = localStorage.getItem('mic')
    if (typeof mic === Boolean) {
      this.callMic(mic)
    }
  },
}
</script>
