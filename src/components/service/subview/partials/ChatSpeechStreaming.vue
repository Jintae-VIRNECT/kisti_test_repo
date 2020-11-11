<template>
  <div class="chat-speech">
    <div class="chat-speech__main">
      <span class="chat-speech__icon active"></span>
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
    ...mapGetters(['mainView', 'translate', 'mic']),
    speechText() {
      return `${this.concatText}
      ${this.outputText}`
    },
  },
  watch: {
    concatText(text) {
      if (text.length > 0) {
        this.doSend(text)
      }
    },
  },
  methods: {
    initStreaming() {
      if (!this.mic.isOn) {
        this.toastError('마이크가 꺼져있습니다.')
        return
      }
      this.startListening(this.mainView.stream)
    },
    doSend(text) {
      this.$call.sendChat(text, this.translate.code)

      this.concatText = ''
    },
  },

  /* Lifecycles */
  mounted() {
    this.initStreaming()
  },
  beforeDestroy() {},
}
</script>
