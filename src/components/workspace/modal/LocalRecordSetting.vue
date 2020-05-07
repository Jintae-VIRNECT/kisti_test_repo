<template>
  <modal
    :visible.sync="visibleFlag"
    :showClose="true"
    :width="546"
    :height="482"
    :beforeClose="beforeClose"
    :class="'local-recsetting-modal'"
  >
    <div class="rec-setting">
      <div class="rec-setting__header">로컬 녹화 설정</div>

      <div class="rec-setting__row">
        <p class="rec-setting__text">녹화대상</p>
        <r-radio
          class="rec-setting__selector"
          :options="radioOption.options"
          :text="radioOption.text"
          :value="radioOption.value"
          :selectedOption.sync="selectRecTarget"
        ></r-radio>
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
        <div class="rec-setting__selector">
          <r-radio
            :options="radioOption.options"
            :text="radioOption.text"
            :value="radioOption.value"
            :selectedOption.sync="selectParticipantRecTarget"
          ></r-radio>
        </div>
      </div>
    </div>
  </modal>
</template>

<script>
import Modal from 'Modal'
import RSelect from 'RemoteSelect'
import RRadio from 'RemoteRadio'

export default {
  name: 'LocalRecordSetting',
  components: {
    Modal,
    RSelect,
    RRadio,
  },
  data() {
    return {
      selectRecTarget: 'workerOnly',
      selectParticipantRecTarget: 'workerOnly',
      visibleFlag: false,

      localRecordingTime: '',
      localRecordingResolution: '',

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
  },
}
</script>

<style lang="scss">
@import '~assets/style/vars';
/** custom mdoal */
.local-recsetting-modal .modal--inner {
  background-color: $color_darkgray_500;
  box-shadow: 0 0 0.714em 0 rgba(255, 255, 255, 0.07),
    0 0.857em 0.857em 0 rgba(255, 255, 255, 0.3);
}
.local-recsetting-modal .modal--header {
  height: 45px;
  padding: 12px 11px 0 0;
  background-color: $color_darkgray_500;
  border-bottom: none;
}

.rec-setting {
  padding: 0;
}

.rec-setting__header {
  margin-bottom: 26px;
  color: $color_white;
  font-size: 18px;
}

.rec-setting__row {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  margin-bottom: 16px;
}

.rec-setting__text {
  width: 162px;
  color: $color_text_main;
  font-size: 14px;
}

.rec-setting__selector {
  align-items: flex-start;
  width: 336px;
}

/*custom remote radio */
.rec-setting {
  .radio-group {
    flex-direction: row;

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
