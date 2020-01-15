<template>
	<div class="video-list" id="video-list">
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
    sessions(val) {
      console.log('sessions CHENGED:::::',val)
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
