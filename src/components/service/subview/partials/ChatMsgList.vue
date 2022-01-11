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
          :class="{ create: chat.status === 'create' }"
          @tts="doTts"
        ></chat-item>
      </ol>
    </vue2-scrollbar>
    <div>
      <transition name="hide-back">
        <chat-input v-if="!usingStt" :speech="usingStt"></chat-input>
      </transition>
      <transition name="hide-bottom">
        <chat-speech
          v-if="isSttSyncAvailable"
          @hidespeech="useStt(false)"
        ></chat-speech>
      </transition>
      <transition name="hide-bottom">
        <chat-speech-streaming
          v-if="isSttStreamAvailable"
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
import { tts } from 'plugins/remote/translate'
import { audio, clearAudio } from 'plugins/remote/tts/audio'

const SCROLL_DELAY = 300
const NO_SCROLL_DELAY = 0

export default {
  name: 'ChatMsgList',
  components: {
    ChatInput,
    ChatItem,
    ChatSpeech,
    ChatSpeechStreaming,
  },
  computed: {
    ...mapGetters(['chatList', 'view', 'translate', 'usingStt', 'speaker']),
    isSttSyncAvailable() {
      return !this.isMobileSize && this.usingStt && this.translate.sttSync
    },
    isSttStreamAvailable() {
      return !this.isMobileSize && this.usingStt && !this.translate.sttSync
    },
  },
  props: {
    show: Boolean,
  },
  watch: {
    chatList: {
      handler() {
        this.$nextTick(() => {
          if (this.show && this.$refs['chatListScrollbar']) {
            const closePopover = false
            this.scrollMsgListToY(NO_SCROLL_DELAY, closePopover)
          }
        })
      },
      deep: true,
    },
    show(val, bVal) {
      this.$nextTick(() => {
        if (val === true && val !== bVal) {
          this.scrollMsgListToY(NO_SCROLL_DELAY)
        }
      })
    },
    view() {
      this.scrollMsgListToY(SCROLL_DELAY)
    },
    usingStt() {
      this.scrollMsgListToY(SCROLL_DELAY)
    },
    'translate.multiple'() {
      this.scrollMsgListToY(SCROLL_DELAY)
    },
  },
  methods: {
    ...mapActions(['useStt']),

    async doTts(message) {
      if (!this.speaker.isOn) return
      if (!this.translate.flag) return
      if (!this.translate.ttsAllow) return
      if (!message || message.length === 0) return
      let ttsText = message
      if (message.length > 200) {
        ttsText = message.substr(0, 200)
      }
      await this.sendTts(ttsText)
      this.doTts(message.substr(200))
    },
    sendTts(text) {
      return new Promise(async resolve => {
        const startTime = Date.now()
        const res = await tts(text, this.translate.code)
        const ttsTime = Date.now() - startTime
        this.logger('TTS', 'DURING TIME: ', ttsTime)
        audio.src = 'data:audio/wav;base64,' + res
        audio.muted = false

        audio.onended = () => {
          resolve(true)
        }
        //oncanplay - User agent가 미디어를 재생 가능한 시점
        audio.oncanplay = async () => {
          audio.play()
        }
        audio.load()
      })
    },
    scrollMsgListToY(delay, closePopover = true) {
      setTimeout(() => {
        if (this.$refs['chatListScrollbar']) {
          this.$refs['chatListScrollbar'].scrollToY(
            Number.MAX_SAFE_INTEGER,
            closePopover,
          )
        }
      }, delay)
    },
  },
  mounted() {
    const noDelay = 0
    this.scrollMsgListToY(noDelay)
  },
  beforeDestroy() {
    clearAudio()
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
