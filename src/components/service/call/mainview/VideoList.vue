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
import { mapGetters } from 'vuex'
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
    ...mapGetters(['sessions'])
  },
	watch: {
    sessions (newVal, oldVal) {
      // console.log(newVal.length, oldVal.length)
      // if(newVal.length > oldVal.length) {
        this.$nextTick(() => {
          if (this.$refs['sessionListScrollbar']) {
            this.$refs['sessionListScrollbar'].scrollToY(999999999)
          }
        })
      // }
    }
  },
	methods: {
    more() {
      console.log('추가 초대하기')
    }
  },

	/* Lifecycles */
	mounted() {

  }
}
</script>
