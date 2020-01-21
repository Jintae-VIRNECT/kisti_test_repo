<template>
	<div class="video-list" id="video-list">
    <vue2-scrollbar ref="sessionListScrollbar" :reverseAxios="true">
    <div class="video-list__view">
      <list-video 
          v-for="session of sessions" 
          :key="session.nodeId" 
          :session="session"></list-video>
      <article>
        <div class="list-video more" @click="more">
          <p>추가 초대하기</p>
        </div>
      </article>
    </div>
    </vue2-scrollbar>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import ListVideo from './partials/ListVideo'
export default {
	name: "VideoList",
	components: {
    ListVideo
  },
	data() {
		return {
      // sessions: [{
      //   nodeId: 1,
      //   stream: '',
      //   nickName: '이름'
      //   sessionName: '세션이름'
      // }]
    }
	},
	computed: {
    ...mapGetters(['sessions', 'mainSession'])
  },
	watch: {
    'sessions.length' :{
      deep: false,
      handler(newVal, oldVal) {
        if(newVal > oldVal) {
          this.$nextTick(() => {
            if (this.$refs['sessionListScrollbar']) {
              this.$refs['sessionListScrollbar'].scrollToX(999999999)
            }
          })
        } else if(newVal < oldVal) {
          let idx = this.sessions.findIndex(session => session.nodeId === this.mainSession.nodeId)
          if(idx < 0) {
            this.setMainSession(this.sessions[0])
          }
          this.$nextTick(() => {
            if (this.$refs['sessionListScrollbar']) {
              this.$refs['sessionListScrollbar'].scrollToX(999999999)
            }
          })
        }
      }
    }
  },
	methods: {
    ...mapActions(['setMainSession']),
    more() {
      console.log('추가 초대하기')
    }
  },

	/* Lifecycles */
	mounted() {

  }
}
</script>
