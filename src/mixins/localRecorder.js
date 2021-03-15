import toastMixin from 'mixins/toast'

import { StreamRecorder } from '@virnect/remote-stream-recorder'
import { mapGetters, mapActions } from 'vuex'
import { getWH, RECORD_TARGET } from 'utils/recordOptions'

import IDBHelper from 'utils/idbHelper'
import { v4 as uuidv4 } from 'uuid'

const logType = 'LocalRecorder'

export default {
  name: 'localRecorder',
  mixins: [toastMixin],
  data() {
    return {
      recorder: null,
      workerJoined: false,

      audioContext: null,
      audioContextDes: null,
      audioSourceMap: new Map(),
      streamMap: new Map(),
      screenStream: null,

      fileCount: 0,
      today: null,
      groupId: null,
      fileName: '',
      totalPlayTime: 0,
      timeMark: 0,
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
      'initing',
      'mainPanoCanvas',
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
    initing(flag, bFlag) {
      if (flag === false && flag !== bFlag) {
        this.participantsChanged()
      }
    },
    participants: {
      handler() {
        this.participantsChanged()
      },
      deep: true,
    },
    resolutions: {
      handler() {
        if (this.recorder !== null) {
          const orientation = this.resolution.orientation
          if (this.localRecordTarget === RECORD_TARGET.WORKER) {
            if (this.mainView.streamMode) {
              this.changeCanvasOrientation(
                orientation,
                this.mainPanoCanvas.captureStream(24),
              )
            } else {
              this.changeCanvasOrientation(orientation, this.mainView.stream)
            }
          } else {
            this.changeCanvasOrientation(null, this.screenStream)
          }
        }
      },
      deep: true,
    },
    mainView: {
      handler(current, before) {
        if (this.recorder !== null) {
          const orientation = this.resolution.orientation

          if (this.localRecordTarget === RECORD_TARGET.WORKER) {
            if (this.mainView.streamMode) {
              if (current.connectionId === before.connectionId) return
              const panoStream = this.mainPanoCanvas.captureStream(24)
              this.changeVideoStream(
                panoStream,
                getWH(
                  this.localRecord.resolution,
                  this.resolution.width,
                  this.resolution.height,
                ),
              )
              this.changeCanvasOrientation(orientation, current.stream)
            } else {
              this.changeVideoStream(
                current.stream,
                getWH(
                  this.localRecord.resolution,
                  this.resolution.width,
                  this.resolution.height,
                ),
              )
              this.changeCanvasOrientation(orientation, current.stream)
            }
          }
        }
      },
      deep: true,
    },
    mainPanoCanvas: {
      handler() {
        if (
          this.mainPanoCanvas &&
          this.localRecordTarget === RECORD_TARGET.WORKER &&
          this.recorder !== null
        ) {
          const canvasStream = this.mainPanoCanvas.captureStream(24)

          this.changeVideoStream(
            canvasStream,
            getWH(
              this.localRecord.resolution,
              this.resolution.width,
              this.resolution.height,
            ),
          )
          this.changeCanvasOrientation(null, canvasStream)
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

    /**
     * participants 변경관련 처리 수행
     */
    participantsChanged() {
      //for stream updated
      this.reconnectAudio()

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

    /**
     * recorder 객체 초기화
     */
    async initRecorder() {
      this.fileCount = 0
      this.groupId = uuidv4()
      this.today = this.$dayjs().format('YYYY-MM-DD HH-mm-ss')

      const config = {}

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
        if (config.streams.length <= 0) {
          throw 'no streams'
        }
      } catch (err) {
        console.error(err)
        throw err
      }

      const maxTime = Number.parseInt(this.localRecord.time, 10)
      const interval = Number.parseInt(this.localRecord.interval, 10)

      config.interval = maxTime < interval ? maxTime : interval

      this.recorder.setConfig(config)

      if (await this.recorder.initRecorder()) {
        const orientation = this.resolution.orientation

        if (this.localRecordTarget === RECORD_TARGET.WORKER) {
          this.changeCanvasOrientation(orientation, this.mainView.stream)
        } else {
          this.changeCanvasOrientation(null, this.screenStream)
        }

        this.recorder.setOndataAvailableCallBack(this.ondataAvailableCallBack)

        return true
      } else {
        this.logger('LocalRecorder', 'initRecorder Failed')
        throw 'initRecorder Failed'
      }
    },

    /**
     * 로컬 녹화 시작
     */
    async startLocalRecord() {
      this.recorder = new StreamRecorder()
      try {
        await IDBHelper.initIDB()
        await this.checkQuota()
        await this.initRecorder()

        this.recorder.startRecord()
        this.timeMark = performance.now()

        this.setLocalRecordStatus('START')
        this.toastDefault(this.$t('service.record_start_message'))
      } catch (err) {
        this.localRecordErrorHandler(err)
      }
    },

    /**
     * 로컬 녹화 종료
     * @param {String} stopType 종료 유형. '', nostream
     */
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
          if (this.localRecordStatus === 'STOP') return

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
        this.setLocalRecordStatus('STOP')
      }
    },

    /**
     * 녹화에 필요한 스트림 생성
     */
    async getStreams() {
      const streams = []
      streams.push(this.audioContextDes.stream)

      if (this.localRecordTarget === RECORD_TARGET.WORKER) {
        const mainStream = this.mainView.stream
        const is360Stream = this.mainView.streamMode

        if (mainStream && mainStream.getVideoTracks().length > 0) {
          if (is360Stream && this.mainPanoCanvas) {
            const canvasStream = this.mainPanoCanvas.captureStream(30)

            const videoStream = new MediaStream()
            videoStream.addTrack(canvasStream.getVideoTracks()[0])
            streams.push(videoStream)
          } else {
            const videoStream = new MediaStream()
            videoStream.addTrack(mainStream.getVideoTracks()[0])
            streams.push(videoStream)
          }
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
      if (
        !navigator.mediaDevices ||
        !navigator.mediaDevices['getDisplayMedia']
      ) {
        throw 'NotSupportDisplayError'
      }
      const screenStream = await navigator.mediaDevices.getDisplayMedia({
        audio: true,
        video: true,
      })

      screenStream.getVideoTracks()[0].onended = () => {
        this.stopLocalRecord()
      }
      this.screenStream = screenStream
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

    /**
     * audioSourceMap에 새로운 participant의 audio stream 연결
     */
    connectAudio() {
      this.participants.forEach(participant => {
        const conId = participant.connectionId

        if (participant.stream && !this.audioSourceMap.has(conId)) {
          if (participant.stream.getAudioTracks().length > 0) {
            let audioSource = this.audioContext.createMediaStreamSource(
              participant.stream,
            )
            audioSource.connect(this.audioContextDes)
            this.audioSourceMap.set(conId, audioSource)
          }
        }
      })
    },

    /**
     * audioSourceMap에 나간 participant의 audio stream 제거
     */
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

    /**
     * 특정 참가자의 오디오를 강제로 해제하고 다시 연결
     *
     * @param {String} conId 커넥션 id
     */
    forceReConnectAudio(conId) {
      if (this.audioSourceMap.get(conId)) {
        this.audioSourceMap.get(conId).disconnect()
        this.audioSourceMap.delete(conId)
      }

      const target = this.participants.find(participant => {
        return participant.connectionId === conId
      })

      if (target.stream) {
        if (target.stream.getAudioTracks().length > 0) {
          let audioSource = this.audioContext.createMediaStreamSource(
            target.stream,
          )
          audioSource.connect(this.audioContextDes)
          this.audioSourceMap.set(conId, audioSource)
        }
      }
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

    async checkQuota() {
      if ((await IDBHelper.checkQuota()) === false) {
        this.logger(logType, 'quota overed cancel recording')
        throw 'quota overed'
      } else {
        return true
      }
    },

    /**
     * 로컬 녹화 시작시 발생하는 에러 처리
     * @param {Object} err 에러 객체
     */
    localRecordErrorHandler(err) {
      if (err && err.name) {
        if (err.name === 'NotAllowedError') {
          if (err.message === 'Invalid state') {
            this.toastError(
              this.$t('service.record_local_browser_not_allow_local_record'),
            )
          } else {
            this.toastError(
              this.$t('service.record_local_blocked_screen_sharing'),
            )
          }
        } else if (err.name === 'NotSupportedError') {
          this.toastError(
            this.$t('service.record_local_browser_not_allow_local_record'),
          )
        } else {
          this.toastError(this.$t('service.record_local_start_failed'))
          console.error(err)
        }
      } else {
        if (err === 'initRecorder Failed') {
          // 초기화 에러
          this.toastError(this.$t('service.record_local_start_failed'))
        } else if (err === 'idb init failed') {
          // idb 초기화 에러
          this.toastError(this.$t('service.record_local_start_failed'))
        } else if (err === 'quota overed') {
          // 용량 없음
          this.toastDefault(this.$t('service.record_fail_memory'))
        } else if (err === 'no streams') {
          // 스트림 없음
          this.toastError(this.$t('service.record_local_start_failed'))
        } else if (err === 'MediaRecorder is not support') {
          this.toastError(
            this.$t('service.record_local_browser_not_allow_local_record'),
          )
        } else if (err === 'NotSupportDisplayError') {
          this.toastError(
            this.$t('service.record_local_browser_not_allow_screen_sharing'),
          )
        } else {
          this.toastError(this.$t('service.record_local_start_failed'))
          console.error(err)
        }
      }
    },

    /**
     * blob을 저장하는 콜백 함수
     * @param {blob} blob 영상 청크
     */
    async ondataAvailableCallBack(blob) {
      //create private uuid for media chunk
      const privateId = uuidv4()

      //make file name
      const fileNumber = this.getFileNumberString(this.fileCount)
      this.fileName =
        this.today + '_' + fileNumber + '_' + this.account.nickname + '.mp4'

      //get media chunk play time
      const currentTime = performance.now()
      const playTime = (currentTime - this.timeMark) / 1000
      this.timeMark = currentTime

      this.totalPlayTime = this.totalPlayTime + playTime / 60

      try {
        await this.checkQuota()
        //insert IDB
        IDBHelper.addMediaChunk(
          this.groupId,
          privateId,
          this.fileName,
          playTime,
          blob.size,
          blob,
          this.account.uuid,
          this.account.nickname,
          this.roomInfo.title,
          this.roomInfo.sessionId,
        )
      } catch (err) {
        console.error(err)
        this.stopLocalRecord()
        await IDBHelper.deleteGroupMediaChunk(this.groupId)
      }

      if (this.totalPlayTime >= this.localRecord.time) {
        this.stopLocalRecord()
      }

      this.fileCount++
    },
    /**
     * 영상의 orientation을 추측
     * @param {MediaStream} mediaStream 판단할 비디오 스트림
     */
    async guessOrientation(mediaStream) {
      if (mediaStream) {
        const tracks = mediaStream.getVideoTracks()
        if (tracks.length > 0) {
          //크롬 getSettings() 버그해결을 위한 슈뢰딩거 코드
          mediaStream.getVideoTracks()[0].getSettings()
          await new Promise(r => setTimeout(r, 100))
          const settings = mediaStream.getVideoTracks()[0].getSettings()

          if (settings.width >= settings.height) {
            return 'landscape'
          } else {
            return 'portrait'
          }
        } else {
          return 'landscape'
        }
      } else {
        return 'landscape'
      }
    },
    /**
     * 녹화용 canvas의 orientation을 지정값 혹은 추측값으로 변경
     *
     * @param {String} orientation 지정할 orientation
     * @param {MediaStream} mediaStream 판단할 미디어 스트림
     */
    async changeCanvasOrientation(orientation, mediaStream) {
      if (orientation && orientation !== '') {
        this.recorder.changeCanvasOrientation(orientation)
      } else {
        const guessedOrientation = await this.guessOrientation(mediaStream)
        this.recorder.changeCanvasOrientation(guessedOrientation)
      }
    },

    /**
     * stream이 변경되는 경우를 위한 업데이트
     */
    reconnectAudio() {
      this.participants.forEach(participant => {
        const conId = participant.connectionId
        if (participant.stream) {
          if (this.streamMap.has(conId)) {
            const streamId = this.streamMap.get(conId)
            if (participant.stream.id !== streamId) {
              this.streamMap.set(conId, participant.stream.id)
              this.forceReConnectAudio(conId)
            }
          } else {
            this.streamMap.set(conId, participant.stream.id)
          }
        }
      })
    },
  },
  mounted() {
    this.$eventBus.$on('localRecord', this.toggleLocalRecordStatus)

    this.audioContext = new (window.AudioContext || window.webkitAudioContext)()
    this.audioContextDes = this.audioContext.createMediaStreamDestination()
  },

  beforeDestroy() {
    this.$eventBus.$off('localRecord', this.toggleLocalRecordStatus)
  },
}
