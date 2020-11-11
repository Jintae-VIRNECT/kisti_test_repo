import {
  getTimeout,
  connect,
  disconnect,
  setStreamingLimit,
  getTranscript,
  getJSON,
} from 'plugins/remote/stt/api'
import { startStreaming, stopStreaming } from 'plugins/remote/stt/audioUtils'
export default {
  data() {
    return {
      isListening: false,

      audio: null,
      restartTime: 0,
      concatText: '   ',
      outputText: '',
      transcriptObject: {},
      transcriptList: [],
      transcriptCounter: 0,
    }
  },
  methods: {
    toggleListen() {
      if (this.isListening) {
        this.stopListening()
      } else {
        this.startListening()
      }
    },
    handleNumberChange(evt) {
      const restartTime = evt.target.validity.valid
        ? evt.target.value
        : this.restartTime
      this.restartTime = restartTime
      setStreamingLimit(restartTime)
    },
    componentWillUnmount() {
      this.stopListening()
      if (this.audioContext) {
        this.audioContext.close()
      }
    },
    startListening(stream) {
      if (this.isListening) return
      this.logger('STT', 'START LISTENING')
      startStreaming(this.audioContext, stream)
      // this.setState({ audio: true, started: true })
      this.isListening = true
      getTranscript((err, transcriptObject) => {
        this.transcriptObject = transcriptObject

        this.transcriptList[this.transcriptCounter] = transcriptObject
        this.logger('STT', 'RECEIVE')

        if (transcriptObject.transcript != undefined) {
          this.outputText = transcriptObject.transcript

          if (transcriptObject.isFinal) {
            this.logger('STT', 'RECEIVED::FINAL::', transcriptObject.transcript)
            this.transcriptCounter = this.transcriptCounter + 1
            this.concatText = transcriptObject.transcript
            this.outputText = transcriptObject.transcript
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
    },
  },
  mounted() {
    this.audioContext = new (window.AudioContext || window.webkitAudioContext)()
    // this.test()
    connect()
  },
  beforeDestroy() {
    this.stopListening()
    disconnect()
  },
}
