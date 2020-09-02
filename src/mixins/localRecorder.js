import toastMixin from 'mixins/toast'

import LocalRecorder from 'utils/localRecorder'
import { mapGetters, mapActions } from 'vuex'
import { ROLE } from 'configs/remote.config'
import { getWH, RECORD_TARGET, LCOAL_RECORD_STAUTS } from 'utils/recordOptions'

export default {
  name: 'LocalRecordMenu',
  mixins: [toastMixin],
  data() {
    return {
      recorder: null,
      workerJoined: false,

      audioContext: null,
      audioContextDes: null,
      audioSourceMap: new Map(),
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
      'localRecordStatus',
      'roomInfo',
    ]),
    /**
     * get resolution of main view
     */
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
    /**
     * check role
     */
    canRecord() {
      if (this.disabled) {
        return false
      }
      if (this.account.roleType === ROLE.LEADER) {
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
      handler() {
        //for joined
        this.connectAudio()

        //for leaved
        this.disconnectAudio()

        if (this.localRecordStatus === LCOAL_RECORD_STAUTS.START) {
          const anyStreamAlive = this.participants.some(participant => {
            return participant.video === true
          })

          if (!anyStreamAlive && this.participants.length > 0) {
            this.$eventBus.$emit('localRecord', {
              isStart: false,
              stopType: 'nostream',
            })
          }
        }
      },
      deep: true,
    },
    resolutions: {
      handler() {
        if (this.recorder !== null) {
          if (this.screenStream === null) {
            this.recorder.changeCanvasOrientation(this.resolution.orientation)
          } else {
            this.recorder.changeCanvasOrientation('landscape')
          }
        }
      },
      deep: true,
    },
    mainView: {
      handler(current) {
        if (this.recorder !== null && this.screenStream === null) {
          this.changeVideoStream(current.stream)
        }
      },
    },
  },
  methods: {
    ...mapActions(['setScreenStream', 'setLocalRecordStatus']),

    async startLocalRecord() {
      this.recorder = new LocalRecorder()

      if (await this.initRecorder()) {
        await this.setLocalRecordStatus(LCOAL_RECORD_STAUTS.START)
        this.recorder.startRecord()
      } else {
        //TODO : MESSAGE
        //녹화 시작 실패시의 안내 메시지
        this.$eventBus.$emit('localRecord', { isStart: false })
        return false
      }
    },

    /**
     * init recorder with config object
     */
    async initRecorder() {
      const config = {}
      config.today = this.$dayjs().format('YYYY-MM-DD HH-mm-ss')
      config.options = {
        video: getWH(
          this.localRecord.resolution,
          this.resolution.width,
          this.resolution.height,
        ),
      }

      try {
        config.streams = await this.getStreams()
      } catch (e) {
        //TODO : MESSAGE
        //필요한 영상, 음성을 가지고 오지 못했을 때에 대한 처리가 필요함.
        console.error(e)
        return false
      }

      config.maxTime = this.localRecord.time
      config.interval = this.localRecord.interval
      config.roomTitle = this.roomInfo.title

      //get nickname
      if (this.account) {
        config.nickname = this.account.nickname
        config.userId = this.account.uuid
      }

      //set callbacks
      this.recorder.setStartCallback(() => {
        this.toastDefault(this.$t('service.record_start_message'))
      })

      this.recorder.setStopSignal(() => {
        this.$eventBus.$emit('localRecord', { isStart: false })
      })

      this.recorder.setNoQuotaCallback(() => {
        this.toastDefault(this.$t('service.record_fail_memory'))
      })

      this.recorder.setConfig(config)

      if (await this.recorder.initRecorder()) {
        if (this.screenStream === null) {
          this.recorder.changeCanvasOrientation(this.resolution.orientation)
        } else {
          this.recorder.changeCanvasOrientation('landscape')
        }

        return true
      } else {
        this.logger('LocalRecorder', 'initRecorder Failed')
        return false
      }
    },

    async stopLocalRecord(stopType) {
      try {
        if (this.recorder) {
          this.recorder.stopRecord()
          if (this.screenStream !== null) {
            this.screenStream.getTracks().forEach(track => {
              track.stop()
            })
          }

          switch (stopType) {
            case 'nostream':
              this.toastDefault(this.$t('service.record_end_no_stream_message'))
              break
            default:
              this.toastDefault(this.$t('service.record_end_message'))
              break
          }
        }
      } catch (e) {
        console.error(e)
      } finally {
        this.recorder = null
        await this.setLocalRecordStatus(LCOAL_RECORD_STAUTS.STOP)
      }
    },

    /**
     * get streams
     */
    async getStreams() {
      const streams = []
      const mainStream = this.mainView.stream

      streams.push(this.audioContextDes.stream)

      switch (this.localRecordTarget) {
        case RECORD_TARGET.WORKER:
          if (mainStream && mainStream.getVideoTracks().length > 0) {
            const videoStream = new MediaStream()
            videoStream.addTrack(mainStream.getVideoTracks()[0])
            streams.push(videoStream)
          }
          break
        case RECORD_TARGET.SCREEN:
          await this.setScreenCapture()

          if (this.screenStream) {
            streams.push(this.screenStream)
          }
          break
        default:
          console.error(
            'Unknown local record target ::',
            this.localRecordTarget,
          )
          break
      }
      return streams
    },

    async setScreenCapture() {
      const displayStream = await navigator.mediaDevices.getDisplayMedia({
        audio: true,
        video: getWH(this.localRecord.resolution),
      })
      this.setScreenStream(displayStream)
    },

    changeVideoStream(videoStream) {
      if (this.localRecordStatus === LCOAL_RECORD_STAUTS.START) {
        this.recorder.changeVideoStream(videoStream)
      }
    },

    stopLocalRecordByKeyPress(e) {
      if (e.key === 'Escape') {
        this.$eventBus.$emit('localRecord', { isStart: false })
      }
    },

    async toggleLocalRecordStatus(status) {
      const isStart = status.isStart
      const stopType = status.stopType

      if (isStart && this.localRecordStatus === LCOAL_RECORD_STAUTS.STOP) {
        this.startLocalRecord()
      } else if (
        !isStart &&
        this.localRecordStatus === LCOAL_RECORD_STAUTS.START
      ) {
        this.stopLocalRecord(stopType)
      }
    },

    connectAudio() {
      this.participants.forEach(participant => {
        const conId = participant.connectionId

        if (participant.stream && !this.audioSourceMap.has(conId)) {
          let audioSource = this.audioContext.createMediaStreamSource(
            participant.stream,
          )
          audioSource.connect(this.audioContextDes)
          this.audioSourceMap.set(conId, audioSource)
        }
      })
    },

    disconnectAudio() {
      for (let conId of this.audioSourceMap.keys()) {
        const isStay = this.participants.some(participant => {
          return participant.connectionId === conId
        })

        if (!isStay) {
          this.audioSourceMap.get(conId).disconnect()
          this.audioSourceMap.delete(conId)
        }
      }
    },
  },
  mounted() {
    this.$eventBus.$on('localRecord', this.toggleLocalRecordStatus)

    this.audioContext = new AudioContext()
    this.audioContextDes = this.audioContext.createMediaStreamDestination()
  },
  beforeDestroy() {
    this.$eventBus.$off('localRecord')
  },
}
