<template>
  <div class="chat-speech">
    <div class="chat-speech__main">
      <span class="chat-speech__icon" :class="{ active: connected }"></span>
      <span class="chat-speech__text">
        {{ `음성 인식된 문장이 표출됩니다.` }}
      </span>
    </div>
    <button class="chat-speech__close" @click="$emit('hidespeech')">
      닫기
    </button>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import toastMixin from 'mixins/toast'
import streamingMixin from './streamingMixin'

export default {
  name: 'ChatSpeechStreaming',
  mixins: [toastMixin, streamingMixin],
  data() {
    return {}
  },
  computed: {
    ...mapGetters(['myInfo', 'translate', 'mic']),
    speechText() {
      return `${this.concatText}
      ${this.outputText}`
    },
  },
  watch: {
    connected(val) {
      if (val) {
        this.initStreaming()
      }
    },
  },
  methods: {
    initStreaming() {
      setTimeout(() => {
        if (!this.myInfo.stream) {
          this.initStreaming()
          return
        }
        this.startListening(this.myInfo.stream)
      }, 100)
    },
    doSend(text) {
      if (text.trim() === '') return
      this.$call.sendChat(text, this.translate.code)
    },
  },

  /* Lifecycles */
  mounted() {},
  beforeDestroy() {},
}
</script>
