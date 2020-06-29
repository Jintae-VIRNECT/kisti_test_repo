<template>
  <menu-button
    text="로컬 녹화"
    :active="isRecording"
    :disabled="!canRecord"
    :src="require('assets/image/ic_local_record.svg')"
    :icActive="isRecording"
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
import { mapGetters, mapActions, mapState } from 'vuex'
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

      today: null,

      userId: 'NONE',
      nickName: 'NONE',

      mixer: null,
    }
  },
  computed: {
    ...mapGetters([
      'participants',
      'mainView',
      'localRecordTarget',
      'screenStream',
      'localRecord',
      'resolutions',
      'control',
    ]),
    ...mapState({
      room: state => state.room,
    }),
    resolution() {
      const idx = this.resolutions.findIndex(
        data => data.connectionId === this.mainView.connectionId,
      )
      if (idx < 0) {
        return {
          width: 0,
          height: 0,
        }
      }
      return this.resolutions[idx]
    },
    canRecord() {
      if (this.disabled) {
        return false
      }
      if (this.account.roleType === ROLE.EXPERT_LEADER) {
        return true
      }
      if (this.control.localRecord) {
        return true
      } else {
        return false
      }
    },
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
          this.stopRecord(showMsg)
        }
      },
    },
    resolutions: {
      handler() {
        if (this.recorder !== null && this.screenStream === null) {
          this.recorder.changeCanvasOrientation(this.resolution.orientation)
        }
      },
      deep: true,
    },
  },
  methods: {
    ...mapActions(['setScreenStream']),

    async recording() {
      if (this.disabled) return
      if (!this.canRecord) {
        // TODO: MESSAGE
        this.toastDefault('리더가 로컬 녹화를 막았습니다. >> 문구정의 필요')
        return
      }
      console.log('recording!!!')

      console.log('this.participants ::', this.participants)
      console.log('this.mainView ::', this.mainView)

      // this.active = 'recording'
      if (!this.isRecording) {
        if (!(await IDBHelper.checkQuota())) {
          console.log('LocalRecording :: quota over!!! cancel recording!!!')
          this.showNoQuota()
          this.$eventBus.$emit('localRecord', false)
          return false
        } else {
          this.startRecord()
          this.$eventBus.$emit('localRecord', true)
        }
      } else {
        const showMsg = true
        this.stopRecord(showMsg)
        this.$eventBus.$emit('localRecord', false)
      }
    },
    async startRecord() {
      this.isRecording = true

      if (!(await this.initRecorder())) {
        return false
      }

      let timeSlice = Number.parseInt(this.localRecord.interval * 1000, 10)
      this.recorder.start(timeSlice)

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
    stopRecord(showMsg) {
      if (this.recorder) {
        this.recorder.stop()
        this.recorder.clearRecordedData()

        if (showMsg && this.isRecording) {
          this.toastDefault(
            '로컬 녹화가 완료되었습니다. 녹화파일 메뉴에서 파일을 확인해 주세요.',
          )
        }
      }

      console.log('localrecord stopped')
      this.isRecording = false
      // this.$openvidu
      //   .stop()
      //   .then(() => {
      //     this.isRecording = false
      //   })
      //   .catch(err => {
      //     console.log(err)
      //   })
    },

    async initRecorder() {
      //for group id
      this.groupId = uuid()

      //for file name
      this.today = this.$dayjs().format('YYYY-MM-DD HH-mm-ss')

      //reset fileCount
      this.fileCount = 0

      const recordStream = await this.getStreams()

      if (recordStream.length <= 0) {
        this.isRecording = false
        return false
      }

      const option = {
        video: this.getWH(this.localRecord.resolution),
      }

      this.recorder = new MSR.MultiStreamRecorder(recordStream, option)
      this.recorder.mimeType = this.mimeType
      // don't provide bit rate option yet
      // this.recorder.videoBitsPerSecond = this.bitrate
      this.recorder.ondataavailable = this.getOndataavailable()
      this.recorder.onstop = () => {
        console.log('stopCallback called')
      }

      return true
    },

    /**
     * get streams
     */
    async getStreams() {
      const streamArray = []
      const participantsAudioStream = []

      //get participants audio stream
      this.participants.forEach(participant => {
        if (participant.stream) {
          const audioTracks = participant.stream.getAudioTracks()

          if (audioTracks && audioTracks.length > 0) {
            const audioStream = new MediaStream()
            const audioTrack = participant.stream.getAudioTracks()[0]

            audioStream.addTrack(audioTrack)
            participantsAudioStream.push(audioStream)
          }
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
          await this.setScreenCapture()

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

    async setScreenCapture() {
      const displayStream = await navigator.mediaDevices.getDisplayMedia({
        audio: true,
        video: this.getWH(this.localRecord.resolution),
      })
      this.setScreenStream(displayStream)
    },

    getWH(resolution) {
      //default
      const video = {
        width: 640,
        height: 480,
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

    getOndataavailable() {
      const ondataavailable = async blob => {
        if (this.fileCount >= 60) {
          console.log('max recording time over')
          this.stopRecord()
        }

        //create private uuid for media chunk
        const privateId = uuid()

        //make file name
        const fileNumber = this.getFileNumberString(this.fileCount)
        this.fileName =
          this.today + '_' + fileNumber + '_' + this.nickname + '.mp4'

        //get media chunk play time
        const currentTime = performance.now()
        const playTime = (currentTime - this.timeMark) / 1000
        this.timeMark = currentTime

        if (!(await IDBHelper.checkQuota())) {
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
            this.userId,
            this.nickname,
            this.room.title,
          )
        }

        this.fileCount++
      }

      return ondataavailable
    },
  },

  /* Lifecycles */
  beforeDestroy() {
    this.stopRecord()
  },
  showNoQuota() {
    this.toastDefault(
      'PC의 용량이 부족하여 녹화를 중지합니다.​ 진행 중이던 녹화파일은 저장되지 않습니다.​',
    )
  },
  async mounted() {
    // await IDBHelper.initIDB()

    //get nickname
    if (this.account && this.account.nickname) {
      this.nickname = this.account.nickname
    }

    //get user uuid
    if (this.account && this.account.uuid) {
      this.userId = this.account.uuid
    }
  },
}
</script>
