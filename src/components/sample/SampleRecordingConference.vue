<template>
  <div class="test">
    <h1 class="test-title">Recording Conference Demo</h1>

    <section class="test-section">
      <h2 class="subtitle">Action</h2>
      <button @click="start">녹화</button>
      <button @click="stop">정지</button>
      <span>{{ callTime | timeFilter }}</span>
      <div>
        <h2 class="subtitle">Options</h2>
        <div>
          <span style="color: #000;">Resolution:</span>
          <select v-model="resolution" :disabled="recording">
            <option
              v-for="(option, idx) in resolutionList"
              :key="idx"
              :value="option.value"
              >{{ option.key }}</option
            >
          </select>
        </div>
        <div>
          <span style="color: #000;">Bitrate:</span>
          <select v-model="bitrate" :disabled="recording">
            <option
              v-for="(option, idx) in bitrateList"
              :key="idx"
              :value="option.value"
              >{{ option.key }}</option
            >
          </select>
        </div>
        <div>
          <span style="color: #000;">통신 속성:</span>
          <p>
            <input
              type="checkbox"
              id="a"
              v-model="export1"
              :disabled="recording"
            />
            <label for="a" style="color: #000;">전문가1 voice</label>
          </p>
          <p>
            <input
              type="checkbox"
              id="b"
              v-model="export2"
              :disabled="recording"
            />
            <label for="b" style="color: #000;">전문가2 voice</label>
          </p>
        </div>
      </div>
    </section>

    <section class="test-section">
      <div class="action-box">
        <div class="video-box">
          <video
            ref="streamVideo"
            :srcObject.prop="stream"
            autoplay
            loop
          ></video>
          <span>작업자</span>
        </div>
        <div>
          <div class="video-box" v-if="export1">
            <video
              class="video-support"
              crossOrigin="anonymous"
              ref="streamVideo2"
              src="~assets/media/send.mp3"
              autoplay
              loop
            ></video>
            <span>전문가1</span>
          </div>
          <div class="video-box" v-if="export2">
            <video
              class="video-support"
              crossOrigin="anonymous"
              ref="streamVideo3"
              src="https://webrtc.github.io/samples/src/video/chrome.webm"
              autoplay
              loop
            ></video>
            <span>전문가2</span>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>
<script>
import RecordRTC from 'recordrtc'
import MultiStreamsMixer from 'multistreamsmixer'

export default {
  components: {},
  data() {
    return {
      stream: null,
      recording: false,
      recorder: null,
      recordOption: {
        type: 'video',
        mimeType: 'video/webm;codecs=vp8',
      },

      resolution: {
        width: 1280,
        height: 720,
      },
      resolutionList: [
        {
          key: '360p',
          value: {
            width: 480,
            height: 360,
          },
        },
        {
          key: '480p',
          value: {
            width: 640,
            height: 480,
          },
        },
        {
          key: '720p',
          value: {
            width: 1280,
            height: 720,
          },
        },
        {
          key: '1080p',
          value: {
            width: 1920,
            height: 1080,
          },
        },
      ],
      bitrate: 8000000,
      bitrateList: [
        {
          key: '100MB bps',
          value: 800000000,
        },
        {
          key: '1MB bps',
          value: 8000000,
        },
        {
          key: '100KB bps',
          value: 800000,
        },
        {
          key: '1KB bps',
          value: 8000,
        },
        {
          key: '100B bps',
          value: 800,
        },
      ],
      export1: false,
      export2: false,

      // timer
      runnerID: null,
      callStartTime: null,
      callTime: null,
    }
  },
  watch: {
    async resolution(after, before) {
      console.log(after, before)
      await this.getStream(after, before)
    },
  },
  methods: {
    async getStream(resolution, bResolution) {
      console.log(resolution)
      try {
        this.stream = await navigator.mediaDevices.getUserMedia({
          video: {
            mandatory: {
              minWidth: resolution.width,
              maxWidth: resolution.width,
              minHeight: resolution.height,
              maxHeight: resolution.height,
            },
          },
          audio: true,
        })
      } catch (err) {
        if (bResolution) {
          this.resolution = bResolution
          alert('지원하지 않는 해상도')
          // this.getStream(bResolution)
        } else {
          console.log(err)
        }
      }
    },
    recordInit() {
      // 작업자 스트림
      // console.log(this.$refs['streamVideo2'])
      // console.log(this.$refs['streamVideo2'].captureStream())
      const mixingStreams = []
      const workerStream = this.stream

      let recordStream = new MediaStream()
      if (!this.export1 && !this.export2) {
        recordStream = workerStream
      } else {
        const audioStream = new MediaStream()
        const audio1Stream = new MediaStream()
        const audio2Stream = new MediaStream()
        audioStream.addTrack(
          workerStream.getTracks().find(track => track.kind === 'audio'),
        )
        mixingStreams.push(audioStream)
        if (this.export1) {
          const expert1Stream = this.$refs['streamVideo2'].captureStream()
          audio1Stream.addTrack(
            expert1Stream.getTracks().find(track => track.kind === 'audio'),
          )
          mixingStreams.push(audio1Stream)
        }
        if (this.export2) {
          const expert2Stream = this.$refs['streamVideo3'].captureStream()
          audio2Stream.addTrack(
            expert2Stream.getTracks().find(track => track.kind === 'audio'),
          )
          mixingStreams.push(audio2Stream)
        }
        // console.log(mixingStreams.getTracks())

        const mixer = new MultiStreamsMixer(mixingStreams)
        recordStream.addTrack(
          mixer
            .getMixedStream()
            .getTracks()
            .find(track => track.kind === 'audio'),
        )
        recordStream.addTrack(
          workerStream.getTracks().find(track => track.kind === 'video'),
        )
      }

      // if (
      //   this.remote.opponentScreenWidth > 1000 ||
      //   this.remote.opponentScreenHeight > 1000
      // ) {
      //   this.recordOption.video.width = this.remote.opponentScreenWidth / 2
      //   this.recordOption.video.height = this.remote.opponentScreenHeight / 2
      // }
      this.recordOption.video = this.resolution
      this.recordOption.bitsPerSecond = this.bitrate

      this.recorder = RecordRTC(recordStream, this.recordOption)
      this.recorder.reset()
    },
    start() {
      this.recordInit()
      this.recording = true
      this.recorder.startRecording()
      this.callStartTime = this.currentTime = this.$dayjs().unix()
      this.timeRunner()
    },
    stop() {
      if (!this.recorder) return
      this.recording = false
      this.recorder.stopRecording()
      this.callStartTime = this.currentTime = null
      clearInterval(this.runnerID)
      setTimeout(() => {
        this.recorder.save(`Remote_Record_${Date.now()}.mp4`)
      }, 1000)
    },
    timeRunner() {
      clearInterval(this.runnerID)
      this.runnerID = setInterval(() => {
        const diff = this.$dayjs().unix() - this.callStartTime

        this.callTime = this.$dayjs.duration(diff, 'seconds').as('milliseconds')
      }, 1000)
    },
  },
  mounted() {
    this.getStream(this.resolution)
  },
}
</script>

<style lang="scss" scoped>
input[type='checkbox'] {
  -webkit-appearance: checkbox;
}
select {
  -webkit-appearance: menulist;
}
.test {
  height: 100%;
  padding: 30px;

  &-title {
    margin-bottom: 30px;
    font-weight: 500;
  }

  &-section {
    margin-bottom: 30px;
    background-color: #bebebe;
    border-radius: 10px;
    .subtitle {
      color: #000;
      font-weight: 500;
    }
    .action-box {
      display: flex;
      .component {
        width: 100%;
      }

      .props {
        flex-shrink: 0;
        width: 250px;
        &-option {
          display: flex;
          margin-bottom: 10px;
        }
        &-title {
          flex-shrink: 0;
          width: 100px;
          padding-right: 10px;
          text-align: right;
        }
        &-options {
          width: 100%;
        }
      }
    }
  }
}
button {
  padding: 5px 15px;
  color: #000;
  border: solid 1px #dedede;
  border-radius: 4px;
}

.video-box {
  position: relative;
  margin-right: 20px;
  margin-bottom: 20px;
  > span {
    position: absolute;
    top: 5px;
    left: 5px;
    display: inline-block;
    padding: 0px 10px;
    color: #fff;
    background: #000;
    border: solid 1px #dedede;
    border-radius: 11px;
  }
}
.video-support {
  width: 200px;
}
</style>
