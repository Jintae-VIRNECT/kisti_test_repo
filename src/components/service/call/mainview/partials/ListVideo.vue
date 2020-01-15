<template>
<article>
	<div
    class="list-video"
    :id="'video-view__'+session.nodeId"
    @click="changeMain">
    <button class="list-video__speaker" @click="mute">음소거</button>
    <div class="list-video__name">
      <span :class="{ 'active': isMain }">{{ session.nickName }}</span>
    </div>
  </div>
</article>
</template>

<script>
import { mapActions } from 'vuex'

export default {
	name: "ListVideo",
	components: {},
	data() {
		return {}
  },
  props: {
    session: Object
  },
	computed: {
    isMain() {
      if (this.session.nodeId === 'main') {
        return true
      }
      return false
    }
  },
	watch: {},
	methods: {
    ...mapActions(['setMainSession']),
    changeMain() {
      console.log(this.session)
      this.setMainSession(this.session)
    },
    mute() {
      this.$openvidu.disconnect(this.session.nodeId)
    }
  },

	/* Lifecycles */
	mounted() {
  }
}
</script>
