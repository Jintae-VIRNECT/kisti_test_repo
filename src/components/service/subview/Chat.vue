<template>
  <div class="chat">
    <div class="chat-header">
      <div class="chat-header__title">
        <div class="chat-header__title--text ">
          {{ roomTitle + ' 원격협업' }}
        </div>
        <!-- <popover trigger="hover" placement="bottom">
          <button slot="reference" class="show-list">목록보기</button>
          <div>사용자 목록</div>
        </popover> -->
      </div>

      <div class="chat-header__menu">
        <div
          class="chat-header__selector"
          :class="{ active: showChat }"
          @click="toggleChatMenu"
        >
          <div class="selector__icon--chat" :class="{ active: showChat }"></div>
          <span
            class="chat-header__selector--text"
            :class="{ active: showChat }"
            >채팅</span
          >
        </div>
        <div
          class="chat-header__selector"
          :class="{ active: showFile }"
          @click="toggleFileMenu"
        >
          <div class="selector__icon--file" :class="{ active: showFile }"></div>
          <span
            class="chat-header__selector--text"
            :class="{ active: showFile }"
            >파일</span
          >
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
          :chat="chat"
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
// import Popover from 'Popover'
// import Tooltip from 'Tooltip'

import ChatMsgBuilder from 'utils/chatMsgBuilder'

export default {
  name: 'Chat',
  components: {
    ChatItem,
    ChatInput,
    // Popover,
    // Tooltip,
  },
  data() {
    return {
      showChat: true,
      showFile: false,

      roomTitle: '',
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
      handler() {
        this.$nextTick(() => {
          if (this.$refs['chatListScrollbar']) {
            this.$refs['chatListScrollbar'].scrollToY(Number.MAX_SAFE_INTEGER)
          }
        })
      },
      deep: true,
    },
    viewMode: {
      handler(newVal) {
        switch (newVal) {
          case 'stream':
            console.log('실시간 공유')
            break
          case 'drawing':
            console.log('협업 보드')
            // this.chatList.push({
            //   text: '협업 보드를 사용합니다.',
            //   name: 'board',
            //   date: new Date(),
            //   uuid: null,
            //   type: 'system',
            // })

            this.chatList.push(
              new ChatMsgBuilder()
                .setType('system')
                .setSubType('board')
                .setText('협업 보드를 사용합니다.')
                .build(),
            )
            break
          case 'ar':
            this.chatList.push(
              new ChatMsgBuilder()
                .setType('system')
                .setSubType('ar')
                .setText('AR 기능을 사용합니다.')
                .build(),
            )
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
  },

  /* Lifecycles */
  mounted() {
    this.roomTitle = this.room.title ? this.room.title : ''

    this.chatList.push(
      new ChatMsgBuilder()
        .setType('system')
        .setSubType('alarm')
        .setText('협업이 생성 되었습니다.')
        .build(),
    )

    //test messages.
    // this.chatList.push(
    //   new ChatMsgBuilder()
    //     .setType('me')
    //     .setName('펭수')
    //     .setText('hihi')
    //     .build(),
    // )

    // this.chatList.push(
    //   new ChatMsgBuilder()
    //     .setType('me')
    //     .setName('펭수')
    //     .setText('hihi')
    //     .setFile([
    //       {
    //         filename: 'Webex.png',
    //         filesize: '10MB',
    //       },
    //     ])
    //     .build(),
    // )

    // this.chatList.push(
    //   new ChatMsgBuilder()
    //     .setType('opponent')
    //     .setName('펭수')
    //     .setText('hihi')
    //     .setFile([
    //       {
    //         filename: 'Webex.png',
    //         filesize: '10MB',
    //       },
    //     ])
    //     .build(),
    // )

    // this.chatList.push(
    //   new ChatMsgBuilder()
    //     .setType('system')
    //     .setName('테스트2', true)
    //     .setSubType('cancel')
    //     .setText('cancel File Transfer')
    //     .build(),
    // )
  },
}
</script>
