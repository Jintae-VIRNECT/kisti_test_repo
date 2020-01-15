<template>
<div class="chat">
  <div class="chat-header">
    <p class="chat-header__title">버넥트 리모트팀 외 5명</p>
    <p class="chat-header__description">6명의 작업자</p>
    <button class="chat-header__fold">펼치기</button>
  </div>
  <vue2-scrollbar ref="chatListScrollbar">
    <ol class="chat-list">
      <li class="chat-item date">
        <p>2020년 01월 06일</p>
      </li>
      <chat-item
          v-for="(chat, idx) of chatList"
          :key="idx"
          :chat="chat"></chat-item>
    </ol>
  </vue2-scrollbar>
  
  <chat-input></chat-input>
</div>
</template>

<script>
import { mapGetters } from 'vuex'

import ChatItem from './partials/ChatItem'
import ChatInput from './partials/ChatInput'

export default {
	name: "Chat",
	components: {
    ChatItem,
    ChatInput
  },
	data() {
		return {
      // chatList: [{
      //   name: '참여자1',
      //   text: '하이염^^',
      //   date: new Date()
      // },{
      //   name: '참여자2',
      //   text: '안녕하심미까',
      //   date: new Date()
      // }]
    }
	},
	computed: {
    ...mapGetters(['chatList'])
  },
	watch: {
    chatList: {
      handler () {
        this.$nextTick(() => {
          if (this.$refs['chatListScrollbar']) {
            this.$refs['chatListScrollbar'].scrollToY(999999999)
          }
        })
      },
      deep: true
    }
  },
	methods: {},

	/* Lifecycles */
	mounted() {}
}
</script>
