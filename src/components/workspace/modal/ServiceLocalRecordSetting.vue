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
        >
        </r-select>
      </div>
      <div class="rec-setting__row">
        <div class="rec-setting__text custom">
          <p>
            녹화간격
          </p>
          <div
            class="rec-setting--tooltip-icon"
            @mouseover="displayTooltip"
            @mouseleave="hideTooltip"
          ></div>
        </div>
        <div class="rec-setting__tooltip--body" v-show="showTooltip">
          <p class="rec-setting__tooltip--text">
            장시간 로컬 녹화 파일 생성 시, PC의 부하 발생할 수 있기 때문에 녹화
            파일을 시간 간격으로 나눠서 생성합니다.
          </p>
        </div>
        <r-select
          class="rec-setting__selector"
          v-on:changeValue="setRecInterval"
          :options="localRecIntervalOpt"
          :value="'value'"
          :text="'text'"
        >
        </r-select>
      </div>
      <div class="rec-setting__row">
        <p class="rec-setting__text">녹화 영상 해상도</p>
        <r-select
          class="rec-setting__selector"
          v-on:changeValue="setRecResolution"
          :options="localRecResOpt"
          :value="'value'"
          :text="'text'"
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

import { mapGetters, mapActions } from 'vuex'

export default {
  name: 'ServiceLocalRecordSetting',
  components: {
    Modal,
    RSelect,
    RCheck,
    RRadio,
  },
  data() {
    return {
      showTooltip: false,

      selectParticipantRecTarget: 'recordWorker',
      visibleFlag: false,

      modalWidth: 568,
      modalHeight: 510,

      modalSmallHeight: 376,

      localRecordingTime: '',
      localRecordingResolution: '',
      joinerPointingApprove: false,

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
      //참여자의 스트림은 ㄴparticipants에서 가지고 오면 됨.
      switch (recordTarget) {
        case 'recordWorker':
          //set worker stream(main view + participants)
          this.setLocalRecordTarget(recordTarget)

          //don't need screen stream when record worker.
          this.setScreenStream(null)
          break
        case 'recordScreen':
          //set screen stream for local record
          this.setLocalRecordTarget(recordTarget)
          //this.setScreenCapture()
          break
        default:
          console.log('unknown value')
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
    },
    setRecInterval(newInterval) {
      this.setLocalRecordInterval(newInterval.value)
    },
    setMic(newMic) {
      this.setMicDevice(newMic.deviceId)
    },
    setSpeaker(newSpeaker) {
      this.setSpeakerDevice(newSpeaker.deviceId)
    },

    setRecResolution(newResolution) {
      if (newResolution.value) {
        this.setRecordResolution(newResolution.value)
      } else {
        this.setRecordResolution(newResolution)
      }
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
    },

    toggleLocalRecording(value) {
      if (value) {
        this.setAllowLocalRecording(true)
      } else {
        this.setAllowLocalRecording(false)
      }
    },

    displayTooltip() {
      this.showTooltip = true
    },
    hideTooltip() {
      this.showTooltip = false
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
    },

    async setScreenCapture() {
      const displayStream = await navigator.mediaDevices.getDisplayMedia({
        audio: true,
        video: {
          width: this.width,
          height: this.height,
        },
      })
      this.setScreenStream(displayStream)
    },
  },

  created() {
    this.init()
  },
}
</script>

<style lang="scss">
@import '~assets/style/vars';
/** custom mdoal */
.modal.local-recsetting-modal .modal--inner {
  background-color: $color_darkgray_500;
  // box-shadow: 0 0 0.714em 0 rgba(255, 255, 255, 0.07),
  //   0 0.857em 0.857em 0 rgba(255, 255, 255, 0.3);
  box-shadow: none;
}
.modal.local-recsetting-modal .modal--header {
  height: 2.813rem;
  padding: 0.75rem 0.688rem 0 0;
  background-color: $color_darkgray_500;
  border-bottom: none;
}

.rec-setting {
  padding: 0;
}

.rec-setting__header {
  margin-bottom: 1.625rem;
  color: $color_white;
  font-size: 1.125rem;
}

.rec-setting__row {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  margin-bottom: 0.875rem;
}

.rec-setting__text {
  width: 12.875rem;
  color: $color_text_main;
  font-size: 0.875rem;

  &.custom {
    display: flex;
  }

  > p {
    margin: 0px 4px 0px 0px;
    color: $color_text_main;
    font-size: 0.875rem;
  }
}

.rec-setting__selector {
  align-items: flex-start;
  width: 21rem;
}

.rec-setting__underbar {
  margin-top: 2.313rem;
  margin-bottom: 2.375rem;
  border-bottom-color: #454548;
  border-bottom-width: 1px;
  border-bottom-style: solid;
}
.rec-setting--tooltip-icon {
  width: 18px;
  height: 18px;
  background: url('~assets/image/ic_tool_tip.svg') center no-repeat;
}
.rec-setting__tooltip--body {
  position: absolute;
  left: 110px;
  z-index: 9999;
  width: 342px;
  height: 68px;
  padding: 16px 24px 16px 28px;
  background: url('~assets/image/img_tooltip_big.svg') center no-repeat;
}

.rec-setting__tooltip--text {
  color: #ffffff;
  font-size: 12px;
}

/*custom remote radio */
.rec-setting {
  .radio-group {
    flex-direction: row;
    padding: 0;

    .radio-option:not(:first-of-type) {
      padding-left: 26px;
    }
  }
}
.modal.local-recsetting-modal {
  .modal--body {
    padding: 0 18px 0 30px;
  }
}
</style>
