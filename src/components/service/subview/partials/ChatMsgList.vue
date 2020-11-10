<template>
  <div class="chat-list">
    <vue2-scrollbar ref="chatListScrollbar" :allowReset="false">
      <ol class="chat-msglist">
        <li class="chat-item date">
          <p>{{ $dayjs().format('LL') }}</p>
        </li>
        <chat-item
          v-for="(chat, idx) of chatList"
          :key="'chat_' + chat.id"
          :beforeChat="idx === 0 ? null : chatList[idx - 1]"
          :afterChat="idx === chatList.length - 1 ? null : chatList[idx + 1]"
          :chat="chat"
        ></chat-item>
      </ol>
    </vue2-scrollbar>
    <chat-input :speech.sync="speech"></chat-input>
    <transition name="hide-bottom">
      <chat-speech
        v-if="speech"
        :sync="translate.sttSync"
        @hidespeech="speech = false"
      ></chat-speech>
    </transition>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import ChatItem from './ChatItem'
import ChatInput from './ChatInput'
import ChatSpeech from './ChatSpeech'
export default {
  name: 'ChatMsgList',
  components: {
    ChatInput,
    ChatItem,
    ChatSpeech,
  },
  data() {
    return {
      speech: false,
    }
  },
  computed: {
    ...mapGetters(['chatList', 'view', 'translate']),
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
    view() {
      setTimeout(() => {
        if (this.$refs['chatListScrollbar']) {
          this.$refs['chatListScrollbar'].scrollToY(Number.MAX_SAFE_INTEGER)
        }
      }, 300)
    },
  },
}
</script>

<style>
.hide-bottom-enter-active,
.hide-bottom-leave-active {
  transition: bottom 0.8s;
}
.hide-bottom-leave-to,
.hide-bottom-enter {
  bottom: -17.143rem;
}
/* .hide-bottom-leave,
.hide-bottom-enter-to {
  bottom: 0;
} */
</style>
