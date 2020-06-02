<template>
  <div class="chat">
    <div class="chat-header">
      <div class="chat-header__title">
        <div class="title-text">{{ roomTitle + ' 원격협업' }}</div>
        <!-- <popover trigger="hover" placement="bottom">
          <button slot="reference" class="show-list">목록보기</button>
          <div>사용자 목록</div>
        </popover> -->
      </div>

      <div class="chat-header__menu">
        <div
          class="menu-selector"
          :class="{ active: showChat }"
          @click="toggleChatMenu"
        >
          <div class="icon chat" :class="{ active__chat: showChat }"></div>
          <span class="text">채팅</span>
        </div>
        <div
          class="menu-selector"
          :class="{ active: showFile }"
          @click="toggleFileMenu"
        >
          <div class="icon file" :class="{ active__file: showFile }"></div>
          <span class="text">파일</span>
        </div>
      </div>
      <!-- <p class="chat-header__description">6명의 작업자</p> -->
      <!-- <tooltip
      class="chat-header__fold"
      content="펼치기">
    </tooltip> -->
      <!-- <button slot="body" class="chat-header__fold">펼치기</button> -->
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
          :chat="hyliter(chat)"
        ></chat-item>
      </ol>
    </vue2-scrollbar>

    <chat-input></chat-input>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { mapState } from 'vuex'

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
      showChat: true,
      showFile: false,

      roomTitle: ' ',
      participantsCount: 1,
    }
  },
  computed: {
    ...mapGetters(['chatList']),
    ...mapState({
      room: state => state.room,
      viewMode: state => state.oncall.view,
    }),
  },
  watch: {
    chatList: {
      handler(newVal, oldVal) {
        this.$nextTick(() => {
          if (this.$refs['chatListScrollbar']) {
            this.$refs['chatListScrollbar'].scrollToY(Number.MAX_SAFE_INTEGER)
          }
        })
      },
      deep: true,
    },
    viewMode: {
      handler(newVal, oldVal) {
        switch (newVal) {
          case 'stream':
            console.log('실시간 공유')
            break
          case 'drawing':
            console.log('협업 보드')
            this.chatList.push({
              text: '협업 보드를 사용합니다.',
              name: 'board',
              date: new Date(),
              uuid: null,
              type: 'system',
            })
            break
          case 'ar':
            this.chatList.push({
              text: 'AR 기능을 사용합니다.',
              name: 'ar',
              date: new Date(),
              uuid: null,
              type: 'system',
            })
            break
        }
      },
    },
  },
  methods: {
    toggleChatMenu() {
      console.log('채팅 clicked')
      if (!this.showChat) {
        this.showChat = true
        this.showFile = false
      }
    },
    toggleFileMenu() {
      console.log('파일 clicked')
      if (!this.showFile) {
        this.showChat = false
        this.showFile = true
      }
    },

    /**
     * @author ykmo
     * Change span tag to p tag for hylite text
     * if span tag changed for other tag. please modifiy this method
     *
     */
    hyliter(chat) {
      let replaced = null

      try {
        let chatText = chat.text

        if (chat.text === undefined && chat.text === null) {
          return chat
        }

        if (chat.type === undefined && chat.type === null) {
          return chat
        }

        //check system type, and text length over 10
        if (chat.type === 'system' && chatText.indexOf('</span>') > 34) {
          replaced = chatText.replace('<span', '<p').replace('</span>', '</p>')
          chat.text = replaced
        }
      } catch (e) {
        console.log(e)
      }

      return chat
    },
  },

  /* Lifecycles */
  mounted() {
    this.roomTitle = this.room.title

    this.chatList.push(
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
        text: '안녕하셔',
        date: new Date(),
      },
      {
        type: 'opponent',
        name: '참여자2',
        text: '그래',
        date: new Date(),
      },
      {
        type: 'opponent',
        name: '참여자2',
        text: '피곤하네 오늘따라 속도 안좋고 애초에 좋았던적이 있던가?',
        date: new Date(),
      },
      {
        type: 'opponent',
        name: '신규사업부 팀장',
        file: [
          {
            filename: 'Webex.png',
            filesize: '10MB',
          },
        ],
        date: new Date(),
      },
      {
        text: '<span class="emphasize">테스트</span>님이 입장하셨습니다.',
        name: 'people',
        date: new Date(),
        uuid: null,
        type: 'system',
      },
      {
        text:
          '<span class="emphasize">이름이 엄청긴분이 오셨습니다.으어어어어어어어어</span>' +
          '님이 입장하셨습니다.',
        name: 'people',
        date: new Date(),
        uuid: null,
        type: 'system',
      },
      {
        text:
          '<span class="emphasize">테스트 팀장</span>' +
          '님이 전송을 취소했습니다.',
        name: 'cancel',
        date: new Date(),
        uuid: null,
        type: 'system',
      },
    )
  },
}
</script>
