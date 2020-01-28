<template>
<tooltip
  content="영상 on/off">
  <toggle-button
    slot="body"
    description="영상 on/off"
    :size="34"
    :active="stream"
    :activeSrc="require('assets/image/call/gnb_ic_video_on.svg')"
    :inactiveSrc="require('assets/image/call/gnb_ic_video_off.png')"
    @action="streamOnOff"
  ></toggle-button>
</tooltip>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'

import Tooltip from 'Tooltip'
import ToggleButton from 'ToggleButton'
export default {
	name: "Stream",
	components: {
    Tooltip,
    ToggleButton,
  },
	data() {
		return {
    }
  },
  computed: {
    ...mapGetters(['stream'])
  },
	watch: {
    stream(){}
  },
	methods: {
    ...mapActions(['callStream']),
    streamOnOff() {

      console.log(this.stream)
      let stream = !this.stream

      this.callStream(stream)      
      this.$openvidu.streamOnOff(stream)
    }
  },

	/* Lifecycles */
	mounted() {}
}
</script>