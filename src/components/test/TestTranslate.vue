<template>
  <div class="translate">
    <h1 class="translate-title">Remote Translate Test</h1>
    <section class="translate-section">
      <h2>STT Example</h2>
      <div class="translate-value">
        <select v-model="sttLanguageCode" @change="changeSttLanguageCode">
          <option
            v-for="language in languages"
            :key="language.sttCode"
            :value="language.sttCode"
            >{{ language.name }}
          </option>
        </select>
        <select v-model="selectAudio">
          <option
            v-for="device in audioDevices"
            :key="device.deviceId"
            :value="device.deviceId"
            >{{ device.label }}
          </option>
        </select>
        <button @click="start">시작</button>
        <button @click="stop">정지</button>
        <!-- <span>{{ restartTime }}</span> -->
      </div>
      <div>
        <p
          class="message"
          v-for="(stt, idx) of sttMessageList"
          :key="'stt' + idx"
        >
          {{ stt.transcript }}
        </p>
      </div>
    </section>

    <section class="translate-section">
      <h2>STT 비동기 TEST</h2>
      <div class="translate-value">
        <select v-model="sttFileLanguageCode">
          <option
            v-for="language in languages"
            :key="language.sttCode"
            :value="language.sttCode"
            >{{ language.name }}
          </option>
        </select>
        <input
          type="file"
          name="file"
          ref="inputFile"
          accept=".mp3,.wav"
          @change="fileUpload($event)"
        />
        <button @click="recordStart">녹화</button>
        <button @click="recordStop">정지</button>
        <p>Time: {{ sttFileTime }}</p>
      </div>
      <div>
        <p class="message">
          {{ sttFileMessage }}
        </p>
      </div>
    </section>

    <section class="translate-section">
      <h2>Translate API Example</h2>
      <div class="translate-value">
        <select v-model="outputLanguage">
          <option
            v-for="language in languages"
            :key="language.code"
            :value="language.code"
            >{{ language.name }}
          </option>
        </select>
        <input v-model="message" @keydown.enter="send" />
        <button @click="send">메시지 전송</button>
        <p>Time: {{ translateTime }}</p>
      </div>
      <div>
        <p class="message" v-for="(chat, idx) of chatList" :key="idx">
          {{ chat }}
        </p>
      </div>
    </section>
  </div>
</template>
<script>
// const speech = require('@google-cloud/speech');
// import speech from '@google-cloud/speech'
import axios from 'axios'
import {
  socket,
  subscribeToTimer,
  setSTTLanguageCode,
  getTranscriptFromJSON,
} from './stt'
import RecordRTC from 'recordrtc'
import Recorder from 'recorder-js'

let bufferSize = 2048
let processor
let input
let globalStream

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
      outputLanguage: 'ko',
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
      sttFileLanguageCode: 'en-US',
      sttFileMessage: '',
      sttFileTime: 0,
      translateTime: 0,
      blob: null,
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

      // subscribeToTimer((err, timestamp) => console.log(timestamp))

      getTranscriptFromJSON((err, transcriptObject) => {
        console.log(transcriptObject)

        if (transcriptObject.isFinal) {
          console.log('FINAL::', transcriptObject)
          this.sttMessageList.push(transcriptObject)
          // this.setState({ transcriptCounter: this.state.transcriptCounter + 1 })
        }
      })
      setSTTLanguageCode(this.sttLanguageCode)
    },
    start() {
      this.startStreaming(this.audioContext)
      subscribeToTimer(this.setRestartTimeInterval)
      // this.setRestartTimeInterval()
    },
    setRestartTimeInterval(val, time) {
      // console.log(time)
    },
    stop() {
      socket.emit('stopStreaming', true)
      const track = globalStream.getTracks()[0]
      track.stop()
      if (input) {
        input.disconnect(processor)
        processor.disconnect()
      }
    },
    startStreaming(context) {
      socket.emit('stopStreaming', true)
      socket.emit('startStreaming', true)
      bufferSize = 2048
      processor = null
      input = null
      globalStream = null

      processor = context.createScriptProcessor(bufferSize, 1, 1)
      processor.connect(context.destination)
      context.resume()

      const constraints = {
        video: false,
        audio: {
          deviceId: this.selectAudio,
        },
      }

      navigator.mediaDevices.getUserMedia(constraints).then(stream => {
        globalStream = stream
        if (input == undefined) {
          input = context.createMediaStreamSource(stream)
        }
        input.connect(processor)

        processor.onaudioprocess = e => {
          this.microphoneProcess(e)
        }
      })
    },
    changeSttLanguageCode() {
      setSTTLanguageCode(this.sttLanguageCode)
    },
    microphoneProcess(e) {
      const left = e.inputBuffer.getChannelData(0)
      const left16 = this.downsampleBuffer(left, 44100, 16000)
      socket.emit('binaryStream', left16)
    },
    downsampleBuffer(buffer, sampleRate, outSampleRate) {
      if (outSampleRate == sampleRate) {
        return buffer
      }
      if (outSampleRate > sampleRate) {
        const e = new Error(
          'downsample rate must be less than original sample rate',
        )
        throw e
      }
      const sampleRateRatio = sampleRate / outSampleRate
      const newLength = Math.round(buffer.length / sampleRateRatio)
      const result = new Int16Array(newLength)
      let offsetResult = 0
      let offsetBuffer = 0
      while (offsetResult < result.length) {
        const nextOffsetBuffer = Math.round(
          (offsetResult + 1) * sampleRateRatio,
        )
        let accum = 0
        let count = 0
        for (
          let i = offsetBuffer;
          i < nextOffsetBuffer && i < buffer.length;
          i++
        ) {
          accum += buffer[i]
          count++
        }

        result[offsetResult] = Math.min(1, accum / count) * 0x7fff
        offsetResult++
        offsetBuffer = nextOffsetBuffer
      }
      return result.buffer
    },

    fileUpload(e) {
      const files = e.target.files

      let reader = new FileReader()
      let startTime = Date.now()

      reader.onloadend = () => {
        // Since it contains the Data URI, we should remove the prefix and keep only Base64 string
        const b64 = reader.result.replace(/^data:.+;base64,/, '')
        console.log(b64) //-> "R0lGODdhAQABAPAAAP8AAAAAACwAAAAAAQABAAACAkQBADs="
        axios
          .post('/stt', {
            file: b64,
            lang: this.sttFileLanguageCode,
            rateHertz: 48000,
          })
          .then(res => {
            console.log(res.data)
            this.sttFileMessage = res.data
            this.sttFileTime = Date.now() - startTime
          })
          .catch(err => {
            console.log(err)
          })
      }

      reader.readAsDataURL(files[0])
    },
    recordStart() {
      const constraints = {
        video: false,
        audio: {
          deviceId: this.selectAudio,
        },
      }
      var chunks = []
      // this.recorder = new Recorder(this.audioContext, {})
      navigator.mediaDevices.getUserMedia(constraints).then(stream => {
        // this.recorder.init(stream)

        this.recorder.start()

        return

        // this.recorder = new MediaRecorder(stream, {
        //   // audioBitsPerSecond: 16000,
        // })
        // console.log(this.recorder)
        // this.$nextTick(() => {
        //   this.recorder.onstop = () => {
        //     let startTime = Date.now()
        //     this.blob = new Blob(chunks, { type: 'audio/wav; codecs=0' })

        //     let blob = new Blob(chunks, {
        //       type: 'audio/wav; codecs=0',
        //     })
        //     var url = URL.createObjectURL(blob)
        //     var a = document.createElement('a')
        //     document.body.appendChild(a)
        //     a.style = 'display: none'
        //     a.href = url
        //     a.download = 'audio.wav'
        //     a.click()
        //     window.URL.revokeObjectURL(url)
        //     chunks = []
        //     let reader = new FileReader()
        //     reader.onload = event => {
        //       console.log(reader.result.replace(/^data:.+;base64,/, ''))
        //       axios
        //         .post('/stt', {
        //           file: reader.result.replace(/^data:.+;base64,/, ''),
        //           lang: this.sttFileLanguageCode,
        //         })
        //         .then(res => {
        //           console.log(res)
        //           this.sttFileMessage = res.data
        //           this.sttFileTime = Date.now() - startTime
        //         })
        //         .catch(err => {
        //           console.log(err)
        //         })
        //     }
        //     console.log(this.blob)
        //     reader.readAsDataURL(this.blob)
        //   }

        //   this.recorder.ondataavailable = e => {
        //     chunks.push(e.data)
        //   }

        //   this.recorder.start()
        // })

        // this.recorder = RecordRTC(stream, {
        //   type: 'audio',
        //   mimeType: 'audio/wav',
        // })
        // this.recorder.startRecording()
      })
    },
    recordStop() {
      this.recorder.stop().then(({ blob, buffer }) => {
        Recorder.download(blob, 'my-audio-file')
      })
      return

      // this.recorder.stopRecording(() => {
      //   this.recorder.getDataURL(dataURL => {
      //     console.log(dataURL.replace(/^data:.+;base64,/, ''))
      //     // let reader = new FileReader()
      //     // reader.onload = event => {
      //     // console.log(event.target.result)
      //     // const b64 = reader.result.replace(/^data:.+;base64,/, '')
      //     this.recorder.save('record.wav')
      //     axios
      //       .post('/stt', {
      //         file: dataURL.replace(/^data:.+;base64,/, ''),
      //         lang: this.sttFileLanguageCode,
      //       })
      //       .then(res => {
      //         console.log(res)
      //         this.sttFileMessage = res.data
      //         this.sttFileTime = Date.now() - startTime
      //       })
      //       .catch(err => {
      //         console.log(err)
      //       })
      //     // }
      //     // reader.readAsDataURL(dataURL)
      //   })
      // })
    },

    send() {
      if (this.message.length === 0) return
      console.log('Bearer gcloud auth application-default print-access-token')
      let startTime = Date.now()
      axios
        .post('/translate', {
          text: this.message,
          target: this.outputLanguage,
        })
        .then(res => {
          console.log(res.data)
          this.chatList.push(res.data)
          this.translateTime = Date.now() - startTime
        })
        .catch(err => {
          console.log(err)
        })
      this.message = ''
    },
  },
  mounted() {
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
