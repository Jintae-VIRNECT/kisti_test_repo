<template>
  <div class="chat">
    <div class="chat-header">
      <div v-show="showSubVideo" class="chat-hedaer__sub-vide">
        <sub-video></sub-video>
      </div>
      <div class="chat-header__title">
        <div class="chat-header__title--text ">
          {{ roomTitle + ' 원격협업' }}
        </div>
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
    </div>

    <chat-msg-list v-if="showChat"></chat-msg-list>
    <chat-input v-if="showChat"></chat-input>

    <chat-file-list v-if="showFile"></chat-file-list>
    <chat-file-down v-if="showFile"></chat-file-down>
  </div>
</template>

<script>
import { mapGetters, mapState } from 'vuex'

import ChatInput from './partials/ChatInput'

import ChatMsgBuilder from 'utils/chatMsgBuilder'
import SubVideo from './SubVideo'

import ChatMsgList from './partials/ChatMsgList'
import ChatFileList from './partials/ChatFileList'
import ChatFileDown from './partials/ChatFileDownload'

export default {
  name: 'Chat',
  components: {
    ChatInput,
    SubVideo,
    ChatMsgList,
    ChatFileList,
    ChatFileDown,
    // Popover,
    // Tooltip,
  },
  data() {
    return {
      showChat: true,
      showFile: false,
      roomTitle: '',
      participantsCount: 1,

      showSubVideo: false,
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
      handler(mode) {
        switch (mode) {
          case 'stream':
            console.log('실시간 공유')
            this.showSubVideo = false
            break
          case 'drawing':
            console.log('협업 보드')
            this.showSubVideo = true

            this.chatList.push(
              new ChatMsgBuilder()
                .setType('system')
                .setSubType('board')
                .setText('협업 보드를 사용합니다.')
                .build(),
            )
            break
          case 'ar':
            this.showSubVideo = true
            this.chatList.push(
              new ChatMsgBuilder()
                .setType('system')
                .setSubType('ar')
                .setText('AR 기능을 사용합니다.')
                .build(),
            )
            break
          default:
            console.log('unknown viewMode :: ', mode)
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
