<template>
  <tooltip :content="`${$t('common.speaker')} on/off`">
    <toggle-button
      slot="body"
      customClass="toggle-header speaker"
      :description="`${$t('common.speaker')} on/off`"
      :size="size"
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
  props: {
    size: {
      type: String,
      default: '2.429rem',
    },
  },
  computed: {
    ...mapGetters(['speaker']),
  },
  methods: {
    ...mapActions(['setDevices']),
    speakerOnOff() {
      let speaker = !this.speaker.isOn

      this.setDevices({
        speaker: {
          isOn: speaker,
        },
      })
      this.$call.sendSpeaker(speaker)
      window.myStorage.setDevice('speaker', 'isOn', speaker)
    },
  },
}
</script>
