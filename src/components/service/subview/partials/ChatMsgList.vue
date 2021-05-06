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
          @tts="doTts"
        ></chat-item>
      </ol>
    </vue2-scrollbar>
    <chat-tts ref="chatTts"></chat-tts>
    <div>
      <transition name="hide-back">
        <chat-input v-if="!usingStt" :speech="usingStt"></chat-input>
      </transition>
      <transition name="hide-bottom">
        <chat-speech
          v-if="usingStt && translate.sttSync"
          @hidespeech="useStt(false)"
        ></chat-speech>
      </transition>
      <transition name="hide-bottom">
        <chat-speech-streaming
          v-if="usingStt && !translate.sttSync"
          @hidespeech="useStt(false)"
        ></chat-speech-streaming>
      </transition>
    </div>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import ChatItem from './ChatItem'
import ChatInput from './ChatInput'
import ChatSpeech from './ChatSpeech'
import ChatSpeechStreaming from './ChatSpeechStreaming'
import ChatTts from './ChatTts'
export default {
  name: 'ChatMsgList',
  components: {
    ChatInput,
    ChatItem,
    ChatSpeech,
    ChatSpeechStreaming,
    ChatTts,
  },
  computed: {
    ...mapGetters(['chatList', 'view', 'translate', 'usingStt']),
  },
  props: {
    show: Boolean,
  },
  watch: {
    chatList: {
      handler() {
        this.$nextTick(() => {
          if (this.show && this.$refs['chatListScrollbar']) {
            this.$refs['chatListScrollbar'].scrollToY(Number.MAX_SAFE_INTEGER)
          }
        })
      },
      deep: true,
    },
    show(val, bVal) {
      this.$nextTick(() => {
        if (val === true && val !== bVal) {
          this.$refs['chatListScrollbar'].scrollToY(Number.MAX_SAFE_INTEGER)
        }
      })
    },
    view() {
      setTimeout(() => {
        if (this.$refs['chatListScrollbar']) {
          this.$refs['chatListScrollbar'].scrollToY(Number.MAX_SAFE_INTEGER)
        }
      }, 300)
    },
    usingStt() {
      setTimeout(() => {
        if (this.$refs['chatListScrollbar']) {
          this.$refs['chatListScrollbar'].scrollToY(Number.MAX_SAFE_INTEGER)
        }
      }, 300)
    },
  },
  methods: {
    ...mapActions(['useStt']),
    doTts(info) {
      this.$refs['chatTts'].doTts(info)
    },
  },
  mounted() {
    this.$refs['chatListScrollbar'].scrollToY(99999)
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
.hide-bottom-leave,
.hide-bottom-enter-to {
  bottom: 0;
}
.hide-back-enter-active,
.hide-back-leave-active {
  transition: bottom 0.8s;
}
.hide-back-leave-to,
.hide-back-enter {
  position: absolute;
}
.hide-back-leave,
.hide-back-enter-to {
  position: absolute;
}
</style>
