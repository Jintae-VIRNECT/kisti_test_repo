<template>
  <div class="chat">
    <div class="chat-header">
      <p class="chat-header__title">
        버넥트 리모트팀 외 5명
        <popover trigger="hover" placement="bottom">
          <button slot="reference" class="show-list">목록보기</button>
          <div>사용자 목록</div>
        </popover>
      </p>
      <p class="chat-header__description">6명의 작업자</p>
      <!-- <tooltip
      class="chat-header__fold"
      content="펼치기">
    </tooltip> -->
      <button slot="body" class="chat-header__fold">펼치기</button>
    </div>

    <vue2-scrollbar ref="chatListScrollbar">
      <ol class="chat-list">
        <li class="chat-item date">
          <p>{{ $dayjs().format('LL') }}</p>
        </li>
        <chat-item
          v-for="(chat, idx) of chatList"
          :key="idx"
          :beforeChat="idx === 0 ? null : chatList[idx - 1]"
          :afterChat="idx === chatList.length - 1 ? null : chatList[idx + 1]"
          :chat="chat"
        ></chat-item>
      </ol>
    </vue2-scrollbar>

    <chat-input></chat-input>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

import ChatItem from './partials/ChatItem'
import ChatInput from './partials/ChatInput'
import Popover from 'Popover'
// import Tooltip from 'Tooltip'

export default {
  name: 'Chat',
  components: {
    ChatItem,
    ChatInput,
    Popover,
    // Tooltip,
  },
  data() {
    return {
      chatList: [
        {
          type: 'me',
          name: '참여자1',
          text: '하이염^^',
          date: new Date(),
        },
        {
          type: 'opponent',
          name: '참여자2',
          text:
            '안녕하심미까안녕하심미까안녕하심미까안녕하심미까안녕하심미까안녕하심미까안녕하심미까안녕하심미까안녕하심미까안녕하심미까안녕하심미까안녕하심미까안녕하심미까안녕하심미까',
          date: new Date(),
        },
        {
          type: 'opponent',
          name: '참여자2',
          text: '하이하이',
          date: new Date(),
        },
        {
          type: 'opponent',
          name: '참여자3',
          text: '파일 전달합니다.',
          file: [
            {
              filename: 'Webex.png',
              filesize: '10MB',
            },
          ],
          date: new Date(),
        },
      ],
    }
  },
  computed: {
    // ...mapGetters(['chatList']),
  },
  watch: {
    chatList: {
      handler() {
        this.$nextTick(() => {
          if (this.$refs['chatListScrollbar']) {
            this.$refs['chatListScrollbar'].scrollToY(999999999)
          }
        })
      },
      deep: true,
    },
  },
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>
