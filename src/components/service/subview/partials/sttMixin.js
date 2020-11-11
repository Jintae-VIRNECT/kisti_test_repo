import Recorder from 'recorder-js'
import { stt } from 'plugins/remote/translate'

export default {
  data() {
    return {
      recording: false,
      record: null,
      sttCode: 'en-US',
    }
  },
  methods: {
    startRecord(code) {
      return new Promise((response, reject) => {
        this.sttCode = code
        this.record
          .start()
          .then(() => {
            this.recording = true
            response(true)
          })
          .catch(err => {
            reject(err)
          })
      })
    },
    stopRecord(doStt = false) {
      if (!this.recording) return
      return new Promise((resolve, reject) => {
        this.record
          .stop()
          .then(async ({ blob, buffer }) => {
            this.recording = false
            let translateText
            if (doStt) {
              translateText = await this.getRecordFile(blob)
            }
            this.logger('STT', 'AUDIO SIZE: ', blob.size)
            resolve(translateText)
            // Recorder.download(blob, 'sttfile.wav')
          })
          .catch(err => {
            this.recording = false
            console.error(err)
            reject(err)
          })
      })
    },
    getRecordFile(blob) {
      return new Promise((resolve, reject) => {
        const startTime = Date.now()
        const reader = new FileReader()
        reader.onload = async () => {
          // console.log(reader.result)
          const b64 = reader.result.replace(/^data:.+;base64,/, '')
          const sendMessage = await stt(b64, this.sttCode)
          const sttTime = Date.now() - startTime
          this.logger('STT', 'MESSAGE: ', sendMessage)
          this.logger('STT', 'DURING TIME: ', sttTime)
          resolve(sendMessage)
          // if (sendMessage.length > 0) {
          //   this.sttText = sendMessage
          // } else {
          //   this.toastDefault('인식된 음성이 없습니다.')
          // }
        }
        reader.readAsDataURL(blob)
      })
    },
    initRecord(stream) {
      const audioContext = new (window.AudioContext ||
        window.webkitAudioContext)()

      this.record = new Recorder(audioContext, {})
      this.record.init(stream)
    },
  },
  beforeDestroy() {
    this.stopRecord()
  },
}
