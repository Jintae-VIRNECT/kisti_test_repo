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
import toastMixin from 'mixins/toast'
import MSR from 'plugins/remote/msr/MediaStreamRecorder.js'

import IDBHelper from 'utils/idbHelper'

import { mapGetters } from 'vuex'
import { ROLE } from 'configs/remote.config'
import uuid from 'uuid'
export default {
  name: 'LocalRecordMenu',
  mixins: [toolMixin, toastMixin],
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

      workerJoined: false,
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
  watch: {
    participants: {
      handler(participants) {
        const checkWorker = participant => {
          return participant.roleType === ROLE.WORKER
        }
        this.workerJoined = participants.some(checkWorker)
      },
      deep: true,
    },
    workerJoined: {
      handler(now, before) {
        //if worker out -> then stop local recording
        if (now === false && before === true) {
          const showMsg = true
          this.stop(showMsg)
        }
      },
    },
  },
  methods: {
    initRecorder() {},

    async recording() {
      console.log('recording!!!')

      console.log('this.participants ::', this.participants)
      console.log('this.mainView ::', this.mainView)

      // this.active = 'recording'
      if (!this.isRecording) {
        if (!(await IDBHelper.checkEstimatedQuota())) {
          console.log('LocalRecording :: quota over!!! cancel recording!!!')
          this.showNoQuota()
          return
        } else {
          this.record()
        }
      } else {
        const showMsg = true
        this.stop(showMsg)
      }
    },
    record() {
      this.isRecording = true

      //for group id
      this.groupId = uuid()

      //reset fileCount
      this.fileCount = 0

      const recordStream = this.getStreams()
      if (recordStream.length <= 0) {
        this.isRecording = false
        return
      }

      const option = {
        video: this.getWH(this.recordResolution),
      }

      this.recorder = new MSR.MultiStreamRecorder(recordStream, option)
      this.recorder.mimeType = this.mimeType

      // don't provide bit rate option yet
      // this.recorder.videoBitsPerSecond = this.bitrate

      let today = this.$dayjs().format('YYYY-MM-DD HH-mm-ss')

      this.recorder.ondataavailable = async blob => {
        if (this.fileCount >= 60) {
          console.log('max recording time over')
          this.stop()
        }

        //create private uuid for media chunk
        let privateId = uuid()

        //make file name
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

        if (!(await IDBHelper.checkEstimatedQuota())) {
          this.recorder.stop()
          this.recorder.clearRecordedData()
          await IDBHelper.deleteGroupMediaChunk(this.groupId)
          this.showNoQuota()
        } else {
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
        }

        this.fileCount++
      }

      const stopCallback = () => {
        console.log('stopCallback called')
      }

      this.recorder.onstop = stopCallback
      this.recorder.start(Number.parseInt(this.localRecordInterval * 1000, 10))

      this.toastDefault(
        '로컬 화면 녹화를 시작합니다. 녹화를 종료하시려면 버튼을 한번 더 클릭하거나 [ESC]키를 누르세요.',
      )

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
    stop(showMsg) {
      this.isRecording = false

      if (this.recorder) {
        this.recorder.stop()
        this.recorder.clearRecordedData()

        if (showMsg) {
          this.toastDefault(
            '로컬 녹화가 완료되었습니다. 녹화파일 메뉴에서 파일을 확인해 주세요.',
          )
        }
      }

      console.log('localrecord stopped')

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

      switch (this.localRecordTarget) {
        case 'recordWorker':
          if (this.mainView.stream) {
            streamArray.push(this.mainView.stream)
          } else {
            console.error('mainView is not vaild!!')
          }
          break
        case 'recordScreen':
          if (this.screenStream) {
            streamArray.push(this.screenStream)
          } else {
            console.error('screenStream is not vaild!!')
          }
          break
        default:
          console.log('local record :: unknown target')
          break
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
  beforeDestroy() {
    console.log('beforeDestroy')
    this.stop()
  },
  showNoQuota() {
    this.toastDefault(
      'PC의 용량이 부족하여 녹화를 중지합니다.​ 진행 중이던 녹화파일은 저장되지 않습니다.​',
    )
  },
  async mounted() {
    await IDBHelper.initIDB()
  },
}
</script>
