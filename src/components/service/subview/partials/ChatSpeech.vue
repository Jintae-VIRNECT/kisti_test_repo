<template>
  <div class="chat-speech">
    <div class="chat-speech__main">
      <svg class="chat-speech__progress" v-if="progress > 0">
        <circle
          stroke="#0f75f5"
          :stroke-dasharray="`${circumference} ${circumference}`"
          :style="`stroke-dashoffset:${strokeDashoffset}`"
          stroke-width="2"
          fill="transparent"
          r="1.857rem"
          cx="1.929rem"
          cy="1.929rem"
        />
      </svg>
      <button
        class="chat-speech__icon"
        :class="{ active: !sync, recording: progress > 0 }"
        @click="clickSpeech"
      ></button>
      <span class="chat-speech__text">{{ speechGuide }}</span>
      <button
        v-if="sync"
        class="chat-speech__send"
        :class="{ inactive: !sendActive }"
        @click="doSend()"
      >
        보내기
      </button>
    </div>
    <div class="chat-speach__textarea" v-if="sync">
      <textarea
        placeholder="음성 녹음을 진행하여 음성 채팅을 시작하세요."
        v-model="speechText"
      />
    </div>
    <button class="chat-speech__close" @click="$emit('hidespeech')">
      닫기
    </button>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import sttMixin from './sttMixin'
import toastMixin from 'mixins/toast'
const MAX_RECORD_TIME = 15

export default {
  name: 'ChatSpeech',
  mixins: [toastMixin, sttMixin],
  data() {
    return {
      timer: null,
      circumference: 52 * Math.PI,
      strokeDashoffset: 0,
      progress: 0,
      speechText: '',
    }
  },
  props: {
    sync: {
      type: Boolean,
      default: true,
    },
  },
  computed: {
    ...mapGetters(['mainView', 'translate', 'mic']),
    speechGuide() {
      if (this.sync) {
        if (this.speechText && this.speechText.length > 0) {
          return '음성 인식 완료'
        }
        if (this.progress > 0) {
          return '음성 인식 중...'
        } else {
          return '음성 대기'
        }
      } else {
        return '음성 인식된 문장이 표출됩니다.'
      }
    },
    sendActive() {
      if (
        this.sync &&
        this.speechText &&
        this.speechText.length > 0 &&
        this.progress === 0
      ) {
        return true
      } else {
        return false
      }
    },
  },
  methods: {
    async startSpeechRecord() {
      if (!this.mic.isOn) {
        this.toastError('마이크가 꺼져있습니다.')
        return
      }
      if (this.timer) clearInterval(this.timer)
      this.initRecord(this.mainView.stream)
      this.speechText = ''
      this.strokeDashoffset = this.circumference
      await this.startRecord(this.translate.code)
      this.timer = setInterval(async () => {
        this.progress += 1
        this.setProgress()
        if (this.progress > MAX_RECORD_TIME) {
          this.clickSpeech()
        }
      }, 1000)
    },
    setProgress() {
      const offset =
        this.circumference -
        (this.progress / MAX_RECORD_TIME) * this.circumference
      this.strokeDashoffset = offset
    },
    async clickSpeech() {
      if (this.sync) {
        if (this.progress > 0) {
          clearInterval(this.timer)
          this.strokeDashoffset = 0
          this.progress = 0
          const text = await this.stopRecord(true)
          if (!text || text.trim().length === 0) {
            this.toastDefault('인식된 음성이 없습니다.')
          } else {
            this.speechText = text
          }
        } else {
          this.startSpeechRecord()
        }
      }
    },
    async doSend() {
      this.$call.sendChat(this.speechText, this.translate.code)

      this.speechText = ''
    },
  },

  /* Lifecycles */
  mounted() {
    if (this.sync) {
      this.startSpeechRecord()
    }
  },
  beforeDestroy() {
    clearInterval(this.timer)
  },
}
</script>
