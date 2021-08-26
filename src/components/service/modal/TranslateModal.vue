<template>
  <modal
    :visible.sync="visibleFlag"
    :beforeClose="beforeClose"
    width="28rem"
    class="translate"
  >
    <div class="translate__layout">
      <div class="translate__header">
        <p>{{ '음성 번역' }}</p>
      </div>
      <div class="translate__body">
        <img src="~assets/image/call/img_view_nomal.svg" />
        <!-- <div class="translate__">
        </div> -->
        <p class="translate__time">{{ `녹음 시간 : ${duration} sec` }}</p>
        <p class="translate__description">
          {{ '음성 번역 기능은 최대 1분간 진행합니다.' }}
        </p>
        <p class="translate__description">
          {{ '마이크에 정확한 발음으로 말해주세요.' }}
        </p>
        <div v-if="!recording" class="translate_textview">
          <textarea v-if="!recording" v-model="sttText" />
        </div>
      </div>
      <div class="translate__footer">
        <button v-if="recording" class="btn" @click="stopRecord(true)">
          {{ '텍스트 변환' }}
        </button>
        <template v-else>
          <button class="btn" @click="startRecord">{{ '재시도' }}</button>
          <button class="btn">{{ '적용' }}</button>
        </template>
      </div>
    </div>
  </modal>
</template>

<script>
import Modal from 'Modal'

import { mapGetters } from 'vuex'
import Recorder from 'recorder-js'
import { stt } from 'plugins/remote/translate'
import toastMixin from 'mixins/toast'

export default {
  name: 'TranslateModal',
  mixins: [toastMixin],
  components: {
    Modal,
  },
  data() {
    return {
      visibleFlag: false,
      recording: false,
      sttText: '',
      duration: 0,
      record: null,

      callStartTime: null,
      timer: null,
    }
  },
  computed: {
    ...mapGetters(['translate', 'languageCodes']),
  },
  props: {
    visible: {
      type: [Boolean, Object],
      default: false,
    },
  },

  watch: {
    visible(flag) {
      if (!!flag === true) {
        this.startRecord()
      }
      this.visibleFlag = !!flag
    },
  },
  methods: {
    beforeClose() {
      this.stopRecord()
      this.$emit('update:visible', false)
    },
    timeRunner() {
      clearInterval(this.timer)
      this.timer = setInterval(() => {
        const diff = this.$dayjs().unix() - this.callStartTime

        this.duration = Math.round(
          this.$dayjs.duration(diff, 'seconds').as('milliseconds') / 1000,
        )
        if (this.duration === 59) {
          this.stopRecord(true)
        }
      }, 1000)
    },
    startRecord() {
      this.record.start().then(() => {
        this.recording = true
        this.duration = 0
        this.callStartTime = this.$dayjs().unix()
        this.timeRunner()
      })
    },
    stopRecord(doStt = false) {
      this.record
        .stop()
        .then(({ blob, buffer }) => {
          this.recording = false
          if (doStt) {
            this.getRecordFile(blob)
          }
          this.debug('audio data', blob)
          this.logger('Audio size: ', blob.size)
          clearInterval(this.timer)
          // Recorder.download(blob, 'sttfile.wav')
        })
        .catch(err => {
          this.recording = false
          console.error(err)
          clearInterval(this.timer)
        })
    },
    getRecordFile(blob) {
      const startTime = Date.now()
      const reader = new FileReader()
      reader.onload = async () => {
        // console.log(reader.result)
        const b64 = reader.result.replace(/^data:.+;base64,/, '')
        const sendMessage = await stt(b64, this.translate.code)
        const sttTime = Date.now() - startTime
        this.logger('STT Message: ', sendMessage)
        this.logger('STT during time: ', sttTime)
        if (sendMessage.length > 0) {
          this.sttText = sendMessage
        } else {
          this.toastDefault(this.$t('service.stt_no_voice'))
        }
      }
      reader.readAsDataURL(blob)
    },
  },
  created() {
    this.audioContext = new (window.AudioContext || window.webkitAudioContext)()
    const constraints = {
      video: false,
      audio: true,
    }

    this.record = new Recorder(this.audioContext, {})
    navigator.mediaDevices.getUserMedia(constraints).then(stream => {
      this.record.init(stream)
    })
  },
  beforeDestroy() {
    this.stopRecord()
  },
}
</script>

<style
  lang="scss"
  src="assets/style/service/service-translate-modal.scss"
></style>
9764908 10485760
