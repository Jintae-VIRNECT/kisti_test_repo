<template>
<div class="main-tools">
  <tool-button
    text="포인팅"
    :active="active === 'pointing'"
    :src="require('assets/image/call/ic_pointing.png')"
    @action="pointing"></tool-button>
  <tool-button
    text="캡쳐 후 공유"
    :active="active === 'capture'"
    :src="require('assets/image/call/icn_capture.png')"
    @action="capture"></tool-button>
  <tool-button
    text="녹화"
    :active="active === 'recording'"
    :src="require('assets/image/call/ic_record_off.png')"
    :onActive="false"
    :activeSrc="require('assets/image/call/ic_record_ing.png')"
    @action="recording"></tool-button>
</div>
</template>

<script>
import ToolButton from './ToolButton'
export default {
	name: "MainTools",
	components: {
    ToolButton
  },
	data() {
		return {
      active: null
    }
	},
	computed: {
  },
	watch: {},
	methods: {
    pointing() {
      console.log('pointing!!!!')
      this.active = 'pointing'
    },
    capture() {
      console.log('capture!!!!')
      this.active = 'capture'
    },
    recording() {
      console.log('recording!!!')
      this.active = 'recording'
    }
  },

  /* Lifecycles */
  beforeDestroy() {
  },
	mounted() {
  }
}
</script>
