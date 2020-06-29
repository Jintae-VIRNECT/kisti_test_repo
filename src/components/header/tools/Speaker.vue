<template>
  <tooltip content="스피커 on/off">
    <toggle-button
      slot="body"
      description="스피커 on/off"
      size="2.429rem"
      :active="speaker.isOn"
      :activeSrc="require('assets/image/call/gnb_ic_volum_on.svg')"
      :inactiveSrc="require('assets/image/call/gnb_ic_volum_off.svg')"
      @action="speakerOnOff"
    ></toggle-button>
  </tooltip>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'

import Tooltip from 'Tooltip'
import ToggleButton from 'ToggleButton'
export default {
  name: 'Speaker',
  components: {
    Tooltip,
    ToggleButton,
  },
  computed: {
    ...mapGetters(['speaker']),
  },
  methods: {
    ...mapActions(['callSpeaker']),
    speakerOnOff() {
      let speaker = !this.speaker
      this.callSpeaker(speaker)

      this.$call.speaker(speaker)
    },
  },

  /* Lifecycles */
  created() {
    let speaker = localStorage.getItem('speaker')
    if (typeof speaker === Boolean) {
      this.callSpeaker(speaker)
    }
  },
}
</script>
