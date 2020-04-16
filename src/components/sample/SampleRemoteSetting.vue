<template>
  <div class="setting">
    <h1 class="setting-title">Remote Setting Test</h1>

    <!-- Video Device 설정 -->
    <section class="setting-section">
      <h2 class="setting-subtitle">Select Video</h2>
      <div>
        <select v-model="selectVideo" @change="getVideoStream">
          <option
            v-for="device in videoDevices"
            :key="device.deviceId"
            :value="device.deviceId"
            >{{ device.label }}</option
          >
        </select>
      </div>
      <video :srcObject.prop="videoStream" autoplay></video>
    </section>

    <!-- Audio Device 설정 -->
    <section class="setting-section">
      <h2 class="setting-subtitle">Select Audio</h2>
      <div>
        <select v-model="selectAudio" @click="getAudioStream">
          <option
            v-for="device in audioDevices"
            :key="device.deviceId"
            :value="device.deviceId"
            >{{ device.label }}</option
          >
        </select>
        <label>볼륨</label>
        <select v-model="audioVolume" @change="changeAudioVolume">
          <option
            v-for="(volume, idx) in volumeList"
            :key="idx"
            :value="volume"
            >{{ volume }}</option
          >
        </select>
        <button @click="$refs['audioComponent'].play()">재생</button>
        <button @click="$refs['audioComponent'].pause()">정지</button>
      </div>
      <audio
        ref="audioComponent"
        :srcObject.prop="audioStream"
        autoplay
      ></audio>
      <meter ref="audioMeter" min="0" max="100" :value="soundWidth"></meter>
      <div class="sound">
        <div class="sound-back"></div>
        <div class="sound-front" :style="{ width: soundWidth + '%' }"></div>
      </div>
    </section>

    <!-- Output Device 설정 -->
    <section class="setting-section">
      <h2 class="setting-subtitle">Select Output</h2>
      <div>
        <select v-model="selectOutput" @change="setAudioOutput">
          <option
            v-for="device in outputDevices"
            :key="device.deviceId"
            :value="device.deviceId"
            >{{ device.label }}</option
          >
        </select>
        <label>볼륨</label>
        <select v-model="outputVolume" @change="changeOutputVolume">
          <option
            v-for="(volume, idx) in volumeList"
            :key="idx"
            :value="volume"
            >{{ volume }}</option
          >
        </select>
        <div class="block">
          <span class="output-volumn-slider">출력 볼륨 슬라이더 웩!</span>
          <!-- 볼륨은 0에서 최대 1까지에요! -->
          <el-slider
            v-model="outputVolume"
            @change="changeOutputVolume"
            :max="1"
            :min="0"
            :step="0.01"
          ></el-slider>
        </div>
        <el-button @click="$refs['outputComponent'].play()">재생</el-button>
        <el-button @click="$refs['outputComponent'].pause()">정지</el-button>
      </div>
      <audio ref="outputComponent" :src="mp3" autoplay></audio>
      <meter ref="outputMeter" min="0" max="100" :value="outputWidth"></meter>
      <div class="sound">
        <div class="sound-back"></div>
        <div class="sound-front" :style="{ width: outputWidth + '%' }"></div>
      </div>
    </section>
  </div>
</template>
<script>
import SoundMeter from 'plugins/remote/soundmeter'

export default {
  data() {
    return {
      volumeList: [0.2, 0.4, 0.6, 0.8, 1],
      /* video */
      videoDevices: [],
      selectVideo: null,
      videoStream: null,
      /* audio */
      audioDevices: [],
      selectAudio: null,
      audioStream: null,
      audioVolume: 1,
      audioSoundVolume: 0,
      audioContext: null,
      audioSoundMeter: null,
      /* output */
      mp3: require('assets/media/queen.mp3'),
      outputDevices: [],
      selectOutput: null,
      outputStream: null,
      outputVolume: 1,
      outputSoundVolume: 0,
      outputSoundMeter: null,

      outputVolumeMax: 1,
      outputVolumeMin: 0,
      outputVolumeStep: 0.01,
    }
  },
  computed: {
    soundWidth() {
      return parseInt(this.audioSoundVolume * 100)
    },
    outputWidth() {
      return parseInt(this.outputSoundVolume * 100)
    },
  },
  methods: {
    init() {
      navigator.mediaDevices
        .enumerateDevices()
        .then(devices => {
          console.log(devices)
          devices.forEach(device => {
            if (device.kind === 'videoinput') {
              this.videoDevices.push(device)
            } else if (device.kind === 'audioinput') {
              this.audioDevices.push(device)
            } else if (device.kind === 'audiooutput') {
              this.outputDevices.push(device)
            }
          })
        })
        .catch(err => {
          console.log(err)
        })
    },
    getVideoStream() {
      console.log(this.selectVideo)
      const constraints = {
        video: {
          deviceId: this.selectVideo,
        },
        audio: false,
      }
      navigator.mediaDevices.getUserMedia(constraints).then(stream => {
        console.log(stream)
        this.videoStream = stream
      })
    },
    getAudioStream() {
      console.log(this.selectAudio)
      const constraints = {
        video: false,
        audio: {
          deviceId: this.selectAudio,
        },
      }
      navigator.mediaDevices.getUserMedia(constraints).then(stream => {
        // Put variables in global scope to make them available to the
        // browser console.
        this.audioStream = stream
        const soundMeter = new SoundMeter(this.audioContext)
        soundMeter.connectToSource(stream, e => {
          if (e) {
            alert(e)
            return
          }
          setInterval(() => {
            this.audioSoundVolume = soundMeter.instant.toFixed(2)
          }, 200)
        })
      })
    },
    setOutputSoundMeter() {
      this.outputSoundMeter = new SoundMeter(this.audioContext)
      console.log(this.outputSoundMeter)
      this.outputSoundMeter.connectToMedia(this.$refs['outputComponent'], e => {
        if (e) {
          alert(e)
          return
        }
        setInterval(() => {
          this.outputSoundVolume = this.outputSoundMeter.instant.toFixed(2)
        }, 200)
      })
    },
    changeAudioVolume() {
      this.$refs['audioComponent'].volume = this.audioVolume
    },
    changeOutputVolume() {
      this.$refs['outputComponent'].volume = this.outputVolume
    },
    setAudioOutput() {
      this.$refs['outputComponent'].pause()
      this.$nextTick(() => {
        this.$refs['outputComponent'].setSinkId(this.selectOutput).then(() => {
          this.$refs['outputComponent'].play()
        })
      })
    },
  },
  mounted() {
    this.init()
    window.onload = () => {
      this.audioContext = new (window.AudioContext ||
        window.webkitAudioContext)()
    }
  },
}
</script>
<style lang="scss">
@import './TestTheme.scss';
</style>
<style lang="scss" scoped>
.setting-section {
  margin: 20px;
  background-color: #dedede;
  border-radius: 10px;
}

.sound {
  position: relative;
  width: 200px;
  height: 30px;
  .sound-back {
    width: 100%;
    height: 100%;
    background-color: #999;
  }
  .sound-front {
    position: absolute;
    top: 0;
    left: 0;
    height: 100%;
    background-color: #9cdcf0;
  }
}
</style>
