<template>
  <div class="chat">
    <div class="chat-header">
      <div v-show="showSubVideo" class="chat-hedaer__sub-vide">
        <sub-video></sub-video>
      </div>
      <div class="chat-header__title">
        <div class="chat-header__title--text ">
          {{ room.title }}
        </div>
      </div>

      <ul class="chat-header__menu">
        <li class="chat-header__selector" :class="{ active: showChat }">
          <button
            class="chat-header__selector--button"
            @click="toggleMenu('chat')"
          >
            <p class="chat-header__selector--text">
              <img src="~assets/image/call/chat_ic_folder_w.svg" />채팅
            </p>
          </button>
        </li>
        <li class="chat-header__selector" :class="{ active: !showChat }">
          <button
            class="chat-header__selector--button"
            @click="toggleMenu('file')"
          >
            <p class="chat-header__selector--text">
              <img src="~assets/image/call/chat_ic_chat_w.svg" />파일
            </p>
          </button>
        </li>
      </ul>
    </div>

    <div class="chat-body">
      <transition name="chat-left">
        <chat-msg-list v-if="showChat"></chat-msg-list>
      </transition>
      <transition name="chat-right">
        <chat-file-list v-if="!showChat"></chat-file-list>
      </transition>
    </div>
  </div>
</template>

<script>
import { mapGetters, mapState } from 'vuex'

import ChatMsgBuilder from 'utils/chatMsgBuilder'
import SubVideo from './SubVideo'

import ChatMsgList from './partials/ChatMsgList'
import ChatFileList from './partials/ChatFileList'

export default {
  name: 'Chat',
  components: {
    SubVideo,
    ChatMsgList,
    ChatFileList,
  },
  data() {
    return {
      show: 'chat',
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
    showChat() {
      if (this.show === 'chat') return true
      else return false
    },
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
    toggleMenu(menu) {
      this.show = menu
    },
  },

  /* Lifecycles */
  mounted() {
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

<style>
.chat-left-enter-active,
.chat-left-leave-active,
.chat-right-enter-active,
.chat-right-leave-active {
  transition: left ease 0.4s;
}
.chat-left-enter,
.chat-left-leave-to {
  left: -100%;
}
.chat-right-enter,
.chat-right-leave-to {
  left: 100%;
}
.chat-left-enter-to,
.chat-left-leave,
.chat-right-enter-to,
.chat-right-leave {
  left: 0;
}
</style>
