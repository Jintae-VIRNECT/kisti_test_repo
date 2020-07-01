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

import LocalRecorder from 'utils/localRecorder'
import { mapGetters, mapActions, mapState } from 'vuex'
import { ROLE } from 'configs/remote.config'
import { getWH, RECORD_TARGET } from 'utils/recordOptions'

export default {
  name: 'LocalRecordMenu',
  mixins: [toolMixin, toastMixin],
  data() {
    return {
      isRecording: false,
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
      if (this.disabled) return false
      if (!this.canRecord) {
        // TODO: MESSAGE
        this.toastDefault('리더가 로컬 녹화를 막았습니다. >> 문구정의 필요')
        return false
      }

      if (!this.isRecording) {
        this.recorder = new LocalRecorder()

        if (!(await this.initRecorder())) {
          return false
        }
        this.recorder.startRecord()
      } else {
        const showMsg = true
        this.stopRecord(showMsg)
      }
    },

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

      config.streams = await this.getStreams()
      config.maxTime = this.localRecord.time
      config.interval = this.localRecord.interval
      config.roomTitle = this.room.title

      //get nickname
      if (this.account && this.account.nickname) {
        config.nickName = this.account.nickname
      }

      //get user uuid
      if (this.account && this.account.uuid) {
        config.userId = this.account.uuid
      }

      //set callbacks
      this.recorder.setStartCallback(() => {
        this.isRecording = true
        this.$eventBus.$emit('localRecord', true)
        this.toastDefault(
          '로컬 화면 녹화를 시작합니다. 녹화를 종료하시려면 버튼을 한번 더 클릭하거나 [ESC]키를 누르세요.',
        )
      })

      this.recorder.setStopCallback(() => {
        this.isRecording = false
        this.$eventBus.$emit('localRecord', false)
      })

      this.recorder.setNoQuotaCallback(() => {
        this.toastDefault(
          'PC의 용량이 부족하여 녹화를 중지합니다.​ 진행 중이던 녹화파일은 저장되지 않습니다.​',
        )
      })

      this.recorder.setConfig(config)
      if (await this.recorder.initRecorder()) {
        this.recorder.changeCanvasOrientation(this.resolution.orientation)
        return true
      } else {
        return false
      }
    },

    stopRecord(showMsg) {
      try {
        if (this.recorder) {
          this.recorder.stopRecord()
          if (showMsg) {
            this.toastDefault(
              '로컬 녹화가 완료되었습니다. 녹화파일 메뉴에서 파일을 확인해 주세요.',
            )
          }
        }
      } catch (e) {
        console.error(e)
      } finally {
        this.isRecording = false
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
  },

  /* Lifecycles */
  beforeDestroy() {
    this.stopRecord()
  },
}
</script>
