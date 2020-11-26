import {
  requestRestarted,
  connect,
  disconnect,
  setStreamingLimit,
  getTranscript,
  splitStreaming,
  getJSON,
} from 'plugins/remote/stt/api'
import { startStreaming, stopStreaming } from 'plugins/remote/stt/audioUtils'
import { mapGetters } from 'vuex'
export default {
  data() {
    return {
      isListening: false,
      connected: false,

      audio: null,
      restartTime: 0,
      audioContext: null,
      outputText: '',
      concatText: '',
    }
  },
  computed: {
    ...mapGetters(['translate']),
  },
  methods: {
    handleNumberChange(evt) {
      const restartTime = evt.target.validity.valid
        ? evt.target.value
        : this.restartTime
      this.restartTime = restartTime
      setStreamingLimit(restartTime)
    },
    startListening(stream) {
      if (this.isListening) return
      this.logger('STT', 'START LISTENING')
      startStreaming(this.audioContext, stream)
      requestRestarted(this.restartStreaming)
      // this.setState({ audio: true, started: true })
      this.isListening = true
      getTranscript((err, transcriptObject) => {
        this.debug(
          'STT',
          'RECEIVE::',
          transcriptObject.isFinal,
          '::',
          transcriptObject.transcript,
        )

        if (transcriptObject.transcript != undefined) {
          this.outputText = transcriptObject.transcript
          if (this.outputText.length > 200) {
            splitStreaming()
          }
          if (transcriptObject.isFinal) {
            this.logger('STT', 'RECEIVED::FINAL::', transcriptObject.transcript)
            this.outputText = transcriptObject.transcript
            if (typeof this.doSend === 'function') {
              this.doSend(this.outputText)
            }
            this.outputText = ''
          }
        }
      })
      getJSON((err, json) => {
        // console.log(json.results)
        // console.log(json.results[0].alternatives[0].transcript)
        // console.log(
        //   json.results[1] ? json.results[1].alternatives[0].transcript : '',
        // )
      })
    },
    stopListening() {
      if (!this.isListening) return
      this.logger('STT', 'STOP LISTENING')
      // this.setState({ audio: false })
      this.isListening = false
      stopStreaming(this.audioContext)
      if (this.audioContext) {
        this.audioContext.close()
      }
    },
    restartStreaming(err, obj) {
      this.logger('STT', `RESTART::DURATION : ${obj.duration}`)
      if (this.outputText.trim().length > 0) {
        this.logger('STT', `RESTART::${this.outputText}`)
        if (typeof this.doSend === 'function') {
          this.doSend(this.outputText)
        }
        this.outputText = ''
      }
    },
  },
  mounted() {
    this.audioContext = new (window.AudioContext || window.webkitAudioContext)()
    connect(this.translate.code)
      .then(() => {
        this.connected = true
      })
      .catch(() => {
        this.connected = false
      })
  },
  beforeDestroy() {
    this.stopListening()
    disconnect()
    if (this.outputText && this.outputText.trim().length > 0) {
      this.logger('STT', 'RECEIVED::END::', this.outputText)
      if (typeof this.doSend === 'function') {
        this.doSend(this.outputText)
      }
    }
  },
}
