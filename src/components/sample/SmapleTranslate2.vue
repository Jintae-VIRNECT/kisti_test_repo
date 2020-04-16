<template>
  <div class="translate">
    <h1 class="translate-title">Remote Translate Test</h1>
    <section class="translate-section">
      <h2>STT 비동기 TEST</h2>
      <div class="translate-value">
        <div>
          <span>입력 스트림 : </span>
          <select v-model="selectAudio">
            <option
              v-for="device in audioDevices"
              :key="device.deviceId"
              :value="device.deviceId"
              >{{ device.label }}
            </option>
          </select>
        </div>
        <div>
          <span>입력 언어 : </span>
          <select v-model="sttLanguageCode">
            <option
              v-for="language in languages"
              :key="language.sttCode"
              :value="language.sttCode"
              >{{ language.name }}
            </option>
          </select>
        </div>
        <div>
          <span>번역 언어 : </span>
          <select v-model="translateLanguageCode">
            <option
              v-for="language in languages"
              :key="language.code"
              :value="language.code"
              >{{ language.name }}
            </option>
          </select>
        </div>
        <button @click="recordStart">녹화</button>
        <button @click="recordStop">정지</button>
      </div>
      <div>
        <p class="message">
          {{ 'STT :' + sttFileMessage }}
        </p>
        <p>STT 소요시간: {{ sttTime }}</p>
        <p class="message">
          {{ 'Translate :' + translateText }}
        </p>
        <p>번역 소요시간: {{ translateTime }}</p>
        <!-- <audio ref="ttsAudio" controls :src="audioSrc"></audio> -->
        <audio preload="auto" ref="ttsAudio" controls autoplay>
          <source :src="audioSrc" />
        </audio>
        <p>TTS 소요시간: {{ ttsTime }}</p>
        <p>TOTAL: {{ sttTime + translateTime + ttsTime }}</p>
      </div>
    </section>
  </div>
</template>
<script>
import axios from 'axios'
import Recorder from 'recorder-js'

export default {
  data() {
    return {
      audioContext: null,
      audioDevices: [],
      selectAudio: null,
      sttLanguageCode: 'en-US',
      sttMessageList: [],
      message: '',
      chatList: [],
      languages: [
        {
          name: '한국어',
          code: 'ko',
          sttCode: 'ko-KR',
        },
        {
          name: '영어',
          code: 'en',
          sttCode: 'en-US',
        },
        {
          name: '중국어(간체)',
          code: 'zh',
          sttCode: 'zh',
        },
        {
          name: '폴란드어',
          code: 'pl',
          sttCode: 'pl-PL',
        },
        {
          name: '우쿠라이나어',
          code: 'uk',
          sttCode: 'uk-UA',
        },
      ],
      recorder: null,
      translateLanguageCode: 'ko',
      sttFileMessage: '',
      translateText: '',
      sttTime: 0,
      ttsTime: 0,
      translateTime: 0,
      blob: null,
      audioSrc: '',
    }
  },
  methods: {
    init() {
      navigator.mediaDevices
        .enumerateDevices()
        .then(devices => {
          console.log(devices)
          devices.forEach(device => {
            if (device.kind === 'audioinput') {
              this.audioDevices.push(device)
            }
          })
        })
        .catch(err => {
          console.log(err)
        })
    },

    recordStart() {
      const constraints = {
        video: false,
        audio: {
          deviceId: this.selectAudio,
        },
      }
      if (!this.selectAudio) {
        constraints.audio = true
      }
      // this.recorder = new Recorder(this.audioContext, {})
      navigator.mediaDevices.getUserMedia(constraints).then(stream => {
        // this.recorder.init(stream)

        this.recorder.start()
      })
    },
    recordStop() {
      let startTime = Date.now()
      this.recorder.stop().then(({ blob, buffer }) => {
        // Recorder.download(blob, 'my-audio-file')

        let reader = new FileReader()
        reader.onload = event => {
          console.log(reader.result)
          const b64 = reader.result.replace(/^data:.+;base64,/, '')
          console.log(reader.result.replace(/^data:.+;base64,/, ''))
          axios
            .post('/stt', {
              file: b64,
              lang: this.sttLanguageCode,
              rateHertz: 48000,
            })
            .then(res => {
              console.log(res.data)
              this.sttFileMessage = res.data
              this.sttTime = Date.now() - startTime
              this.send(this.sttFileMessage)
            })
            .catch(err => {
              console.log(err)
            })
        }
        console.log(blob)
        reader.readAsDataURL(blob)
      })
    },

    send(message) {
      if (message.length === 0) return
      console.log('translate message::', message)
      let startTime = Date.now()
      axios
        .post('/translate', {
          text: message,
          target: this.translateLanguageCode,
        })
        .then(res => {
          console.log(res.data)
          this.translateText = res.data
          this.translateTime = Date.now() - startTime
          this.tts()
        })
        .catch(err => {
          console.log(err)
        })
    },
    tts() {
      let startTime = Date.now()
      axios
        .post('/tts', {
          text: this.translateText,
          lang: this.translateLanguageCode,
        })
        .then(res => {
          this.ttsTime = Date.now() - startTime
          console.log(res.data)
          this.audioSrc = 'data:audio/wav;base64,' + res.data
          this.$refs['ttsAudio'].load()
        })
    },
  },
  mounted() {
    return
    this.audioContext = new (window.AudioContext || window.webkitAudioContext)()
    this.init()
    // const speech = require('@google-cloud/speech')
    // console.log(speech)

    const constraints = {
      video: false,
      audio: {
        deviceId: this.selectAudio,
      },
    }

    this.recorder = new Recorder(this.audioContext, {})
    navigator.mediaDevices.getUserMedia(constraints).then(stream => {
      this.recorder.init(stream)
    })
  },
}
</script>

<style lang="scss" scoped>
button {
}
.translate-title {
  margin: 20px;
}
.translate-section {
  margin: 20px;
  background-color: #dedede;
  border-radius: 10px;
}
.translate-control {
  display: inline;
}
.translate-serverkey {
  min-width: 600px;
  padding: 8px 15px;
  color: #fff;
  background-color: #787878;
  border: solid 1px #fff;
  border-radius: 4px;
}
.translate-button {
  margin-top: 20px;
  padding: 8px 15px;
  color: #fff;
  background-color: #267bff;
  border-radius: 4px;
}

.translate-value {
  margin-top: 20px;
  padding: 10px;
  background-color: #fff;
  border-radius: 4px;
  & > span {
    word-break: break-word;
  }
}

.message {
  width: fit-content;
  margin: 2px;
  padding: 2px 4px;
  color: #fff;
  background-color: #267bff;
  border-radius: 4px;
}
</style>
