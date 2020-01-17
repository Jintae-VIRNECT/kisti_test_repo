<template>
<article>
	<div
    class="list-video"
    :class="{ 'current': isCurrent }"
    :id="'video-view__'+session.nodeId"
    @click="changeMain">
    <toggle-button
      v-if="!isMain"
      customClass="list-video__speaker"
      description="음소거 on/off"
      :active="onSpeaker"
      :activeSrc="require('assets/image/call/gnb_ic_voice_on.png')"
      :inactiveSrc="require('assets/image/call/gnb_ic_voice_off.png')"
      :size="30"
      @action="mute"
    ></toggle-button>
    
    <div v-if="!isMain" class="list-video__status good">
      <span>우수</span>
    </div>
    <div class="list-video__name">
      <span :class="{ 'active': isMain }">{{ session.nickName }}</span>
    </div>
  </div>
</article>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'

import ToggleButton from 'ToggleButton'

export default {
	name: "ListVideo",
	components: {
    ToggleButton
  },
	data() {
		return {
      onSpeaker: true
    }
  },
  props: {
    session: Object
  },
	computed: {
    ...mapGetters(['mainSession']),
    isMain() {
      if (this.session.nodeId === 'main') {
        return true
      }
      return false
    },
    isCurrent() {
      if(this.mainSession.nodeId === this.session.nodeId) 
        return true
      return false
    }
  },
	watch: {},
	methods: {
    ...mapActions(['setMainSession']),
    changeMain() {
      this.setMainSession(this.session)
    },
    mute(e) {
      e.preventDefault();
      e.stopPropagation();
      
      this.onSpeaker = !this.onSpeaker
      this.$openvidu.audioOnOff(this.session.nodeId, this.onSpeaker)
    },
  },

	/* Lifecycles */
	mounted() {
  }
}
</script>
