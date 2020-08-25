import toastMixin from 'mixins/toast'

import LocalRecorder from 'utils/localRecorder'
import { mapGetters, mapActions } from 'vuex'
import { ROLE } from 'configs/remote.config'
import { getWH, RECORD_TARGET, LCOAL_RECORD_STAUTS } from 'utils/recordOptions'
import { logger } from 'utils/logger'

export default {
  name: 'LocalRecordMenu',
  mixins: [toastMixin],
  data() {
    return {
      recorder: null,
      workerJoined: false,
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
      'allowLocalRecord',
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
      if (this.allowLocalRecord) {
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
          this.$eventBus.$emit('localRecord', false)
        }
      },
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
  },
  methods: {
    ...mapActions(['setScreenStream', 'setLocalRecordStatus']),

    async startRecord() {
      this.recorder = new LocalRecorder()

      if (await this.initRecorder()) {
        await this.setLocalRecordStatus(LCOAL_RECORD_STAUTS.START)
        this.recorder.startRecord()
      } else {
        //TODO : MESSAGE
        //녹화 시작 실패시의 안내 메시지
        this.$eventBus.$emit('localRecord', false)
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
        this.$eventBus.$emit('localRecord', false)
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
        logger('LocalRecorder', 'initRecorder Failed')
        return false
      }
    },

    /**
     * stop recorder
     * @param {Boolean} showMsg show toast msg
     */
    async stopRecord(showMsg) {
      try {
        if (this.recorder) {
          this.recorder.stopRecord()
          if (this.screenStream !== null) {
            this.screenStream.getTracks().forEach(track => {
              track.stop()
            })
          }

          if (showMsg) {
            this.toastDefault(this.$t('service.record_end_message'))
          }
        }
      } catch (e) {
        console.error(e)
      } finally {
        await this.setLocalRecordStatus(LCOAL_RECORD_STAUTS.STOP)
      }
    },

    /**
     * get streams
     */
    async getStreams() {
      const streams = []
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
        streams.push(...participantsAudioStream)
      }

      switch (this.localRecordTarget) {
        case RECORD_TARGET.WORKER:
          if (this.mainView.stream) {
            streams.push(this.mainView.stream)
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

    stopLocalRecordByKeyPress(e) {
      if (e.key === 'Escape') {
        this.$eventBus.$emit('localRecord', false)
      }
    },
    async toggleStatus(isStart) {
      if (isStart && this.localRecordStatus === LCOAL_RECORD_STAUTS.STOP) {
        this.startRecord()
      } else if (
        !isStart &&
        this.localRecordStatus === LCOAL_RECORD_STAUTS.START
      ) {
        const showMsg = true
        this.stopRecord(showMsg)
      }
    },
  },
  mounted() {
    this.$eventBus.$on('localRecord', this.toggleStatus)
  },
  beforeDestroy() {
    this.$eventBus.$off('localRecord')
  },
}
