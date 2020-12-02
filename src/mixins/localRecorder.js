import toastMixin from 'mixins/toast'

import LocalRecorder from 'utils/localRecorder'
import { mapGetters, mapActions } from 'vuex'
import { getWH, RECORD_TARGET } from 'utils/recordOptions'

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
      screenStream: null,
    }
  },
  computed: {
    ...mapGetters([
      'participants',
      'mainView',
      'localRecordTarget',
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
  },
  watch: {
    'participants.length': 'participantsChanged',
    resolutions: {
      handler() {
        if (this.recorder !== null) {
          if (this.localRecordTarget === RECORD_TARGET.WORKER) {
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
        if (
          this.recorder !== null &&
          this.localRecordTarget === RECORD_TARGET.WORKER
        ) {
          this.recorder.changeCanvasOrientation(this.resolution.orientation)

          this.changeVideoStream(
            current.stream,
            getWH(
              this.localRecord.resolution,
              this.resolution.width,
              this.resolution.height,
            ),
          )
        }
      },
    },
    allowLocalRecord(allow) {
      if (allow === false && this.localRecordStatus === 'START') {
        this.toastDefault(this.$t('service.record_blocked'))
        this.stopLocalRecord()
      }
    },
  },
  methods: {
    ...mapActions(['setLocalRecordStatus']),
    participantsChanged() {
      //for joined
      this.connectAudio()

      //for leaved
      this.disconnectAudio()

      if (this.localRecordStatus === 'START') {
        if (this.participants.length === 0) return
        const anyStreamAlive = this.participants.some(participant => {
          return participant.video === true
        })

        if (!anyStreamAlive) {
          this.stopLocalRecord('nostream')
        }
      }
    },

    async startLocalRecord() {
      this.recorder = new LocalRecorder()
      try {
        await this.initRecorder()
        this.recorder.startRecord()
        this.setLocalRecordStatus('START')
        this.toastDefault(this.$t('service.record_start_message'))
      } catch (err) {
        if (err && err.name) {
          if (err.name === 'NotAllowedError') {
            this.toastDefault(this.$t('화면 공유 접근이 차단되었습니다.'))
          } else if (err.name === 'NotSupportedError') {
            this.toastDefault(
              this.$t('로컬 녹화를 지원하지 않는 브라우저 입니다.'),
            )
          }
        } else {
          if (err === 'initRecorder Failed') {
            // 초기화 에러
            this.toastDefault(this.$t('로컬 녹화에 실패하였습니다.'))
          } else if (err === 'idb init failed') {
            // idb 초기화 에러
            this.toastDefault(this.$t('로컬 녹화에 실패하였습니다.'))
          } else if (err === 'quota overed') {
            // 용량 없음
          } else if (err === 'no streams') {
            // 스트림 없음
          }
        }
      }
    },

    /**
     * init recorder with config object
     */
    async initRecorder() {
      const config = {}
      config.today = this.$dayjs().format('YYYY-MM-DD HH-mm-ss')

      if (this.localRecordTarget === RECORD_TARGET.WORKER) {
        config.options = {
          video: getWH(
            this.localRecord.resolution,
            this.resolution.width,
            this.resolution.height,
          ),
        }
      } else {
        config.options = {
          video: getWH(this.localRecord.resolution),
        }
      }

      try {
        config.streams = await this.getStreams()
      } catch (err) {
        // 필요한 영상, 음성을 가지고 오지 못했을 때에 대한 처리
        throw err
      }

      config.maxTime = this.localRecord.time
      config.interval = this.localRecord.interval

      if (
        Number.parseInt(this.localRecord.time, 10) <
        Number.parseInt(this.localRecord.interval, 10)
      ) {
        config.interval = this.localRecord.time
      }
      config.roomTitle = this.roomInfo.title
      config.sessionId = this.roomInfo.sessionId

      //get nickname
      if (this.account) {
        config.nickname = this.account.nickname
        config.userId = this.account.uuid
      }

      this.recorder.setStopSignal(() => {
        this.stopLocalRecord()
      })

      this.recorder.setNoQuotaCallback(() => {
        this.toastDefault(this.$t('service.record_fail_memory'))
      })

      this.recorder.setConfig(config)

      if (await this.recorder.initRecorder()) {
        if (this.localRecordTarget === RECORD_TARGET.WORKER) {
          this.recorder.changeCanvasOrientation(this.resolution.orientation)
        } else {
          this.recorder.changeCanvasOrientation('landscape')
        }

        return true
      } else {
        this.logger('LocalRecorder', 'initRecorder Failed')
        throw 'initRecorder Failed'
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
            this.screenStream = null
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
        await this.setLocalRecordStatus('STOP')
      }
    },

    /**
     * get streams
     */
    async getStreams() {
      const streams = []
      streams.push(this.audioContextDes.stream)

      if (this.localRecordTarget === RECORD_TARGET.WORKER) {
        const mainStream = this.mainView.stream
        if (mainStream && mainStream.getVideoTracks().length > 0) {
          const videoStream = new MediaStream()
          videoStream.addTrack(mainStream.getVideoTracks()[0])
          streams.push(videoStream)
        }
      } else {
        await this.setScreenCapture()

        if (this.screenStream) {
          streams.push(this.screenStream)
        }
      }
      return streams
    },

    async setScreenCapture() {
      const displayStream = await navigator.mediaDevices.getDisplayMedia({
        audio: true,
        video: getWH(this.localRecord.resolution),
      })

      displayStream.getVideoTracks()[0].onended = () => {
        this.stopLocalRecord()
      }
      this.screenStream = displayStream
    },

    changeVideoStream(videoStream, resolution) {
      if (this.localRecordStatus === 'START') {
        this.recorder.changeVideoStream(videoStream, resolution)
      }
    },

    stopLocalRecordByKeyPress(e) {
      if (e.key === 'Escape') {
        this.stopLocalRecord()
      }
    },

    async toggleLocalRecordStatus() {
      if (this.localRecordStatus === 'START') {
        this.stopLocalRecord()
      } else {
        this.startLocalRecord()
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

    this.audioContext = new (window.AudioContext || window.webkitAudioContext)()
    this.audioContextDes = this.audioContext.createMediaStreamDestination()
  },
  beforeDestroy() {
    this.$eventBus.$off('localRecord')
  },
}
