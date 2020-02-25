<template>
<article>
	<div
    class="list-video"
    :class="{ 'current': isCurrent }"
    :id="'video-view__'+session.nodeId"
    @click="changeMain">
    <img 
      v-if="!isMain"
      class="list-video__speaker"
      :src="session.audio ? require('assets/image/call/gnb_ic_voice_on.svg') 
                   : require('assets/image/call/gnb_ic_voice_off.svg')"
    />
    
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

export default {
	name: "ListVideo",
	components: {
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
    ...mapGetters(['mainSession', 'speaker']),
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
	watch: {
    speaker(val) {
      if(this.$el.querySelector('video')) {
        this.$el.querySelector('video').muted = val
      }
    }
  },
	methods: {
    ...mapActions(['setMainSession']),
    changeMain() {
      this.setMainSession(this.session)
    },
    mute(e) {
      e.preventDefault();
      e.stopPropagation();
      
      this.onSpeaker = !this.onSpeaker
      // this.$openvidu.audioOnOff(this.session.nodeId, this.onSpeaker)
    },
  },

	/* Lifecycles */
	mounted() {
  }
}
</script>
