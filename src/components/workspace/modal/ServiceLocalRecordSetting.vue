<template>
  <modal
    :visible.sync="visibleFlag"
    :showClose="true"
    :height="showPoiniting ? modalHeight : modalSmallHeight"
    :width="modalWidth"
    :beforeClose="beforeClose"
    :class="'local-recsetting-modal'"
  >
    <div class="rec-setting">
      <div v-if="showPoiniting" class="rec-setting__header">포인팅 설정</div>
      <div v-if="showPoiniting" class="rec-setting__row">
        <p class="rec-setting__text">참가자 포인팅</p>

        <r-check
          :text="'참가자 포인팅 허용'"
          :value="'allowPointing'"
          @toggle="toggleAllowPointing"
        ></r-check>
      </div>

      <div v-if="showPoiniting" class="rec-setting__underbar" />

      <div class="rec-setting__header">로컬 녹화 설정</div>

      <div class="rec-setting__row">
        <p class="rec-setting__text">녹화대상</p>
        <div class="rec-setting__selector">
          <r-radio
            :options="radioOption.options"
            :text="radioOption.text"
            :value="radioOption.value"
            :selectedOption.sync="selectParticipantRecTarget"
          ></r-radio>
        </div>
      </div>
      <div class="rec-setting__row">
        <p class="rec-setting__text">최대 녹화 시간</p>
        <r-select
          class="rec-setting__selector"
          v-on:changeValue="setRecLength"
          :options="localRecTimeOpt"
          :value="'value'"
          :text="'text'"
          :defaultValue="localRecordLength"
        >
        </r-select>
      </div>
      <div class="rec-setting__row">
        <div class="rec-setting__text custom">
          <p>
            녹화 간격
          </p>
          <popover
            placement="right"
            trigger="hover"
            popperClass="custom-popover"
            width="25.4286rem"
          >
            <div slot="reference" class="rec-setting--tooltip-icon"></div>
            <div class="rec-setting__tooltip--body">
              <p class="rec-setting__tooltip--text">
                장시간 로컬 녹화 파일 생성 시, PC의 부하 발생할 수 있기 때문에
                녹화 파일을 시간 간격으로 나눠서 생성합니다.
              </p>
            </div>
          </popover>
        </div>
        <r-select
          class="rec-setting__selector"
          v-on:changeValue="setRecInterval"
          :options="localRecIntervalOpt"
          :value="'value'"
          :text="'text'"
          :defaultValue="localRecordInterval"
        >
        </r-select>
      </div>

      <div class="rec-setting__row">
        <div class="rec-setting__text custom">
          <p>
            녹화 영상 해상도
          </p>
          <popover
            placement="right"
            trigger="hover"
            popperClass="custom-popover"
            width="25.4286rem"
          >
            <div slot="reference" class="rec-setting--tooltip-icon"></div>
            <div class="rec-setting__tooltip--body">
              <p class="rec-setting__tooltip--text">
                720p(HD)급이상 해상도 설정 시, PC의 성능에 따라 서비스가
                원활하지 않을 수 있습니다.
              </p>
            </div>
          </popover>
        </div>

        <r-select
          class="rec-setting__selector"
          v-on:changeValue="setRecResolution"
          :options="localRecResOpt"
          :value="'value'"
          :text="'text'"
          :defaultValue="recordResolution"
        >
        </r-select>
      </div>
      <div class="rec-setting__row">
        <div class="rec-setting__text">참가자 로컬 녹화</div>
        <r-check
          :text="'참가자 로컬 녹화 허용'"
          :value="'allowLocalRecording'"
          @toggle="toggleLocalRecording"
        ></r-check>
      </div>
    </div>
  </modal>
</template>

<script>
import Modal from 'Modal'
import RSelect from 'RemoteSelect'
import RCheck from 'RemoteCheckBox'
import RRadio from 'RemoteRadio'
import Popover from 'Popover'

import toastMixin from 'mixins/toast'

import { mapGetters, mapActions } from 'vuex'

export default {
  name: 'ServiceLocalRecordSetting',
  mixins: [toastMixin],
  components: {
    Modal,
    RSelect,
    RCheck,
    RRadio,
    Popover,
  },
  data() {
    return {
      selectParticipantRecTarget: 'recordWorker',
      visibleFlag: false,

      modalWidth: '40.5714rem',
      modalHeight: '36.4286rem',

      modalSmallHeight: '26.8571rem',

      localRecordingTime: '',
      localRecordingResolution: '',
      joinerPointingApprove: false,

      toastFlag: false,

      localRecTimeOpt: [
        {
          value: '5',
          text: '5분',
        },
        {
          value: '10',
          text: '10분',
        },
        {
          value: '15',
          text: '15분',
        },
        {
          value: '30',
          text: '30분',
        },
        {
          value: '60',
          text: '60분',
        },
      ],

      localRecResOpt: [
        {
          value: '360p',
          text: '360p',
        },
        {
          value: '480p',
          text: '480p',
        },
        {
          value: '720p',
          text: '720p',
        },
      ],

      radioOption: {
        options: [
          {
            text: '영상 녹화',
            value: 'recordWorker',
          },
          {
            text: '화면 녹화',
            value: 'recordScreen',
          },
        ],
        text: 'text',
        value: 'value',
      },
      localRecIntervalOpt: [
        {
          value: '60',
          text: '1분',
        },
      ],
    }
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    showPoiniting: {
      type: Boolean,
      default: true,
    },
  },

  computed: {
    ...mapGetters([
      'localRecordLength',
      'localRecordInterval',
      'micDevice',
      'speakerDevice',
      'recordResolution',
      'allowPointing',
      'allowLocalRecording',
      'screenStream',
    ]),
  },

  watch: {
    visible(flag) {
      this.visibleFlag = flag
    },

    selectParticipantRecTarget(recordTarget) {
      console.log(recordTarget)

      switch (recordTarget) {
        case 'recordWorker':
          //set worker stream(main view + participants)
          this.setLocalRecordTarget(recordTarget)

          //don't need screen stream when record worker.
          this.setScreenStream(null)
          this.showToast()
          break
        case 'recordScreen':
          //set screen stream for local record
          this.setLocalRecordTarget(recordTarget)
          this.showToast()
          break
        default:
          console.log('unknown recordTarget ::', recordTarget)
          break
      }
    },
  },
  methods: {
    ...mapActions([
      'setLocalRecordLength',
      'setLocalRecordInterval',
      'setMicDevice',
      'setSpeakerDevice',
      'setRecordResolution',
      'setAllowPointing',
      'setAllowLocalRecording',
      'setScreenStream',
      'setLocalRecordTarget',
    ]),

    setRecLength(newRecLength) {
      this.setLocalRecordLength(newRecLength.value)
      this.showToast()
    },
    setRecInterval(newInterval) {
      this.setLocalRecordInterval(newInterval.value)
      this.showToast()
    },
    setMic(newMic) {
      this.setMicDevice(newMic.deviceId)
      this.showToast()
    },
    setSpeaker(newSpeaker) {
      this.setSpeakerDevice(newSpeaker.deviceId)
      this.showToast()
    },

    setRecResolution(newResolution) {
      if (newResolution.value) {
        this.setRecordResolution(newResolution.value)
      } else {
        this.setRecordResolution(newResolution)
      }
      this.showToast()
    },

    beforeClose() {
      this.$emit('update:visible', false)
    },

    toggleAllowPointing(value) {
      if (value) {
        this.setAllowPointing(true)
      } else {
        this.setAllowPointing(false)
      }
      this.showToast()
    },

    toggleLocalRecording(value) {
      if (value) {
        this.setAllowLocalRecording(true)
      } else {
        this.setAllowLocalRecording(false)
      }
      this.showToast()
    },

    init() {
      const time = localStorage.getItem('recordingTime')
      if (time) {
        this.setLocalRecordLength(time)
      }

      const interval = localStorage.getItem('recordingInterval')
      if (interval) {
        this.setLocalRecordInterval(interval)
      }

      const micDefault = localStorage.getItem('micDevice')
      if (micDefault) {
        this.setMic(micDefault)
      }

      const speakerDefault = localStorage.getItem('speakerDevice')
      if (speakerDefault) {
        this.setSpeaker(speakerDefault)
      }

      const resolution = localStorage.getItem('recordingResolution')
      if (resolution) {
        this.setRecResolution(resolution)
      }

      const allowPointing = localStorage.getItem('setAllowPointing')
      if (allowPointing) {
        this.setAllowPointing(allowPointing)
      }

      const allowLocalRecording = localStorage.getItem('allowLocalRecording')
      if (allowLocalRecording) {
        this.setAllowLocalRecording(allowLocalRecording)
      }

      this.toastFlag = true
    },
    showToast() {
      if (this.toastFlag) {
        this.toastNotify('변경사항을 저장했습니다.')
      }
    },
  },

  created() {
    this.init()
  },
}
</script>
