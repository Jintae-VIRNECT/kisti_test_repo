<template>
  <div class="chat">
    <div class="chat-header">
      <div class="chat-header__title">
        <div class="chat-header__title--text ">
          {{ roomInfo.title }}
        </div>
      </div>

      <ul class="chat-header__menu" v-if="useStorage">
        <li class="chat-header__selector" :class="{ active: showChat }">
          <button
            class="chat-header__selector--button"
            @click="toggleMenu('chat')"
          >
            <p class="chat-header__selector--text">
              <img src="~assets/image/call/chat_ic_chat_w.svg" />
              {{ $t('service.chat') }}
            </p>
          </button>
        </li>
        <li class="chat-header__selector" :class="{ active: !showChat }">
          <button
            class="chat-header__selector--button"
            @click="toggleMenu('file')"
          >
            <p class="chat-header__selector--text">
              <img src="~assets/image/call/chat_ic_folder_w.svg" />
              {{ $t('service.file') }}
            </p>
          </button>
        </li>
      </ul>
    </div>

    <div class="chat-body">
      <transition name="chat-left">
        <chat-msg-list v-show="showChat" :show="showChat"></chat-msg-list>
      </transition>
      <transition name="chat-right">
        <chat-file-list v-show="!showChat" :show="!showChat"></chat-file-list>
      </transition>
    </div>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import { ACTION } from 'configs/view.config'

import ChatMsgList from './partials/ChatMsgList'
import ChatFileList from './partials/ChatFileList'

export default {
  name: 'Chat',
  components: {
    ChatMsgList,
    ChatFileList,
  },
  data() {
    return {
      show: 'chat',
    }
  },
  computed: {
    ...mapGetters([
      'chatList',
      'roomInfo',
      'view',
      'viewAction',
      'allowLocalRecord',
      'allowPointing',
      'useStorage',
    ]),
    showChat() {
      return this.show === 'chat'
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
    viewAction(val, oldVal) {
      if (val !== oldVal) {
        if (val === ACTION.AR_POINTING) {
          this.addChat({
            status: 'ar-pointing',
            type: 'system',
          })
          // AR 포인팅을 시작합니다.
          return
        }
        if (val === ACTION.AR_DRAWING) {
          this.addChat({
            status: 'ar-area',
            type: 'system',
          })
          // AR 영역이 설정되었습니다.
          return
        }
      }
    },
    allowLocalRecord(val, bVal) {
      if (val !== bVal) {
        if (val === true) {
          this.addChat({
            status: 'record-allow',
            type: 'system',
          })
        } else {
          this.addChat({
            status: 'record-not-allow',
            type: 'system',
          })
        }
      }
    },
    allowPointing(val, bVal) {
      if (val !== bVal) {
        if (val === true) {
          this.addChat({
            status: 'pointing-allow',
            type: 'system',
          })
        } else {
          this.addChat({
            status: 'pointing-not-allow',
            type: 'system',
          })
        }
      }
    },
  },
  methods: {
    ...mapActions(['addChat']),
    toggleMenu(menu) {
      this.show = menu
    },
  },

  /* Lifecycles */
  mounted() {
    this.addChat({
      type: 'system',
      status: 'create',
    })
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
