<template>
  <div class="chat-speech">
    <div class="chat-speech__main">
      <svg class="chat-speech__progress" v-if="progress > -1">
        <circle
          stroke="#0f75f5"
          :stroke-dasharray="`${circumference} ${circumference}`"
          :style="`stroke-dashoffset:${strokeDashoffset}`"
          stroke-width="2"
          fill="transparent"
          :r="isMobileSize ? '2.8rem' : '1.857rem'"
          :cx="isMobileSize ? '3rem' : '1.929rem'"
          :cy="isMobileSize ? '3rem' : '1.929rem'"
        />
      </svg>
      <button
        class="chat-speech__icon"
        :class="{ recording: progress > -1 }"
        @click="clickSpeech"
      ></button>
      <span class="chat-speech__text">{{ speechGuide }}</span>
      <button
        class="chat-speech__send"
        :class="{ inactive: !sendActive }"
        @click="doSend()"
      >
        {{ $t('button.send') }}
      </button>
    </div>
    <div class="chat-speach__textarea">
      <textarea
        :placeholder="$t('service.stt_sync_placeholder')"
        :disabled="status !== 'wait' || speechText.length === 0"
        v-model="chatText"
      />
    </div>
    <button class="chat-speech__close" @click="$emit('hidespeech')">
      {{ $t('button.close') }}
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
      //circumference: 60 * Math.PI,
      strokeDashoffset: 0,
      progress: -1,
      chatText: '',
      speechText: '',
    }
  },
  computed: {
    ...mapGetters(['myInfo', 'translate', 'mic']),
    speechGuide() {
      if (this.status === 'wait') {
        return this.$t('service.stt_sync_wait')
      } else if (this.status === 'recording') {
        return this.$t('service.stt_sync_recording')
      } else {
        return this.$t('service.stt_sync_complete')
      }
    },
    sendActive() {
      return this.chatText && this.chatText.length > 0 && this.progress === -1
    },
    circumference() {
      if (this.isMobileSize) return 60 * Math.PI
      else return 52 * Math.PI
    },
  },
  methods: {
    async startSpeechRecord() {
      if (!this.mic.isOn) {
        this.toastError(this.$t('service.stt_mic_off'))
        return
      }
      if (this.timer) clearInterval(this.timer)
      this.initRecord(this.myInfo.stream)
      this.chatText = ''
      this.speechText = ''
      this.strokeDashoffset = this.circumference
      try {
        await this.startRecord(this.translate.code)
        this.timer = setInterval(async () => {
          this.progress += 1
          this.setProgress()
          if (this.progress >= MAX_RECORD_TIME) {
            this.clickSpeech()
          }
        }, 1000)
        this.progress = 0
        this.setProgress()
      } catch (err) {
        this.toastError(this.$t('service.stt_sync_error'))
      }
    },
    setProgress() {
      const offset =
        this.circumference -
        (this.progress / MAX_RECORD_TIME) * this.circumference
      this.strokeDashoffset = offset
    },
    async clickSpeech() {
      if (this.progress > -1) {
        clearInterval(this.timer)
        const text = await this.stopRecord(true)
        this.strokeDashoffset = 0
        this.progress = -1
        if (!text || text.trim().length === 0) {
          this.toastDefault(this.$t('service.stt_no_voice'))
        } else {
          this.speechText = text
          this.chatText = text
        }
      } else {
        this.startSpeechRecord()
      }
    },
    async doSend() {
      this.$call.sendChat(this.chatText, this.translate.code)

      this.chatText = ''
      this.speechText = ''
    },
  },

  /* Lifecycles */
  mounted() {
    this.startSpeechRecord()
  },
  beforeDestroy() {
    clearInterval(this.timer)
  },
}
</script>
