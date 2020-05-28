<template>
  <modal
    :visible.sync="visibleFlag"
    :showClose="true"
    :width="568"
    :height="462"
    :beforeClose="beforeClose"
    :class="'local-recsetting-modal'"
  >
    <div class="rec-setting">
      <div class="rec-setting__header">포인팅 설정</div>
      <div class="rec-setting__row">
        <p class="rec-setting__text">참가자 포인팅</p>

        <r-check
          :text="'참가자 포인팅 허용'"
          :value="'AllowJoinerPointing'"
          @toggle="updateCheckBox"
        ></r-check>
      </div>

      <div class="rec-setting__underbar" />

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
          :value="'AllowJoinerLocalRecording'"
          @toggle="updateCheckBox"
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

export default {
  name: 'LocalRecordSetting',
  components: {
    Modal,
    RSelect,
    RCheck,
    RRadio,
  },
  data() {
    return {
      selectRecTarget: 'workerOnly',
      selectParticipantRecTarget: 'workerOnly',
      visibleFlag: false,

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
            value: 'workerOnly',
          },
          {
            text: '화면 녹화',
            value: 'recordAll',
          },
        ],
        text: 'text',
        value: 'value',
      },
    }
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    // ...mapState({
    //   localRecordLength: state => state.settings.localRecordLength,
    //   recordResolution: state => state.settings.recordResolution,
    // }),
  },
  watch: {
    visible(flag) {
      this.visibleFlag = flag
    },
    selectRecTarget(value) {
      console.log(value)
    },
    selectParticipantRecTarget(value) {
      console.log(value)
    },
  },
  methods: {
    beforeClose() {
      this.$emit('update:visible', false)
    },

    setRecLength: function(newRecLength) {
      console.log(newRecLength)
      //this.$store.dispatch('setLocalRecordLength', newRecLength.value)
    },

    setRecResolution: function(newResolution) {
      console.log(newResolution)
      //this.$store.dispatch('setRecordResolution', newResolution.value)
    },
    updateCheckBox(value) {
      console.log(value)
    },
  },
}
</script>

<style lang="scss">
@import '~assets/style/vars';
/** custom mdoal */
.local-recsetting-modal .modal--inner {
  background-color: $color_darkgray_500;
  // box-shadow: 0 0 0.714em 0 rgba(255, 255, 255, 0.07),
  //   0 0.857em 0.857em 0 rgba(255, 255, 255, 0.3);
  box-shadow: none;
}
.local-recsetting-modal .modal--header {
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
