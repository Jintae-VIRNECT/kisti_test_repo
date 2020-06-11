<template>
  <menu-button
    text="로컬 녹화"
    :active="isRecording"
    :src="require('assets/image/ic_local_record.svg')"
    :onActive="isRecording"
    :activeSrc="require('assets/image/ic_local_record_on.svg')"
    @click="recording"
  ></menu-button>
</template>

<script>
// @TODO:detach related record logics - ykmo

import toolMixin from './toolMixin'
import MSR from 'plugins/remote/msr/MediaStreamRecorder.js'

import IDBHelper from 'utils/idbHelper'

//나중에 다운로드 할때 가져다 써야함
//import RecordRTC from 'recordrtc'

import { mapGetters } from 'vuex'
import uuid from 'uuid'
export default {
  name: 'LocalRecordMenu',
  mixins: [toolMixin],
  data() {
    return {
      isRecording: false,

      recorder: null,

      //may change if provide more options
      mimeType: 'video/webm;codecs=vp9',

      fileCount: 0,
      fileName: '',

      //for media chunk play time
      timeMark: 0,

      //for file chunk group id
      groupId: null,
    }
  },
  computed: {
    ...mapGetters([
      'participants',
      'mainView',
      'localRecordTarget',
      'screenStream',
      'localRecordInterval',
      'recordResolution',
    ]),
  },
  watch: {},
  methods: {
    initRecorder() {},

    recording() {
      console.log('recording!!!')
      console.log('this.participants ::', this.participants)
      console.log('this.mainView ::', this.mainView)

      // this.active = 'recording'
      if (!this.isRecording) {
        this.record()
      } else {
        this.stop()
      }
    },
    record() {
      // this.begin = performance.now()
      this.groupId = uuid()
      this.fileCount = 0
      this.isRecording = true

      const recordStream = this.getStreams()

      const option = {
        video: this.getWH(this.recordResolution),
      }

      this.recorder = new MSR.MultiStreamRecorder(recordStream, option)

      // don't provide bit rate option yet
      // this.recorder.videoBitsPerSecond = this.bitrate

      this.recorder.mimeType = this.mimeType

      //db 인서트
      this.recorder.ondataavailable = blob => {
        //RecordRTC.invokeSaveAsDialog(blob, 'test.mp4')

        //create private uuid for media chunk
        let privateId = uuid()

        //make file name
        let today = this.$dayjs().format('YYYY-MM-DD HH-mm-ss')
        let fileNumber = this.getFileNumberString(this.fileCount)
        this.fileName = today + '_' + fileNumber + '.mp4'

        console.log(this.fileName)
        console.log(blob)

        //get media chunk play time
        let currentTime = performance.now()
        let playTime = (currentTime - this.timeMark) / 1000
        this.timeMark = currentTime

        console.log(playTime)

        //get nickname
        let nickname = 'unknown'
        if (this.account && this.account.nickname) {
          nickname = this.account.nickname
        }

        //insert IDB
        IDBHelper.addMediaChunk(
          this.groupId,
          privateId,
          this.fileName,
          playTime,
          blob.size,
          blob,
          nickname,
        )

        this.fileCount++
      }

      const stopCallback = () => {
        console.log('stopCallback called')
      }

      this.recorder.onstop = stopCallback
      this.recorder.start(Number.parseInt(10 * 1000, 10))
      //this.recorder.start(Number.parseInt(this.localRecordInterval * 1000, 10))
      this.timeMark = performance.now()

      console.log('record started')

      // this.$openvidu
      //   .record()
      //   .then(() => {
      //     this.isRecording = true
      //   })
      //   .catch(err => {
      //     console.log(err)
      //   })
    },
    stop() {
      this.isRecording = false
      this.recorder.stop()
      this.recorder.clearRecordedData()

      // this.end = performance.now()

      // this.$openvidu
      //   .stop()
      //   .then(() => {
      //     this.isRecording = false
      //   })
      //   .catch(err => {
      //     console.log(err)
      //   })
    },

    /**
     * record에 필요한 스트림을 가지고 온다.
     */
    getStreams() {
      const streamArray = []
      const participantsAudioStream = []

      this.participants.forEach(participant => {
        if (participant.stream) {
          let audioStream = new MediaStream()
          let audioTrack = participant.stream.getAudioTracks()[0]

          audioStream.addTrack(audioTrack)
          participantsAudioStream.push(audioStream)
        }
      })

      if (participantsAudioStream.length > 0) {
        streamArray.push(...participantsAudioStream)
      }

      if (this.localRecordTarget === 'recordScreen') {
        if (this.screenStream) {
          streamArray.push(this.screenStream)
        } else {
          //스크린 영상 녹화하려는데 screenStream이 없다? 그럼 녹화 안해!
          //이에 대한 예외 처리 해야겠군
        }
      } else {
        if (this.mainView.stream) {
          //비디오 트랙이 없을수가 있지. 음음 이것도 처리해줘야할것같아
          // const mainViewVideoTrack = this.mainView.stream.getVideoTracks()[0]
          // let mainViewVideoStream = new MediaStream()
          // mainViewVideoStream.addTrack(mainViewVideoTrack)
          // streamArray.push(mainViewVideoStream)

          streamArray.push(this.mainView.stream)
        } else {
          console.log('mainView is not vaild!!')
        }
      }

      if (streamArray.length === 0) {
        console.error('local record :: streamArray is empty!! ')
      }

      return streamArray
    },
    getWH(resolution) {
      //default
      const video = {
        width: 1280,
        height: 720,
      }

      switch (resolution) {
        case '360p':
          video.width = 480
          video.height = 360
          break
        case '480p':
          video.width = 640
          video.height = 480
          break

        case '720p':
          video.width = 1280
          video.height = 720
          break

        default:
          console.log('unknown resolution ::', resolution)
          break
      }
      return video
    },

    /**
     * get file number 0 ~ 59
     * 60 over is not exits
     */
    getFileNumberString(number) {
      let count = ''
      if (number < 10) {
        count = '0' + number
      } else {
        count = number.toString(10)
      }
      return count
    },
  },

  /* Lifecycles */
  beforeDestroy() {},
  async mounted() {
    console.log(this.account)
    await IDBHelper.initIDB()
  },
}
</script>
