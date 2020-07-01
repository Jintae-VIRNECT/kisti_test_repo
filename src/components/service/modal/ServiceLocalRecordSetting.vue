<template>
  <modal
    :visible.sync="visibleFlag"
    :showClose="true"
    :width="'40.5714rem'"
    :beforeClose="beforeClose"
    :class="'local-recsetting-modal'"
  >
    <div class="rec-setting">
      <template v-if="isLeader">
        <p class="rec-setting__header">포인팅 설정</p>
        <div class="rec-setting__row underbar">
          <p class="rec-setting__text">참가자 포인팅</p>

          <r-check
            :text="'참가자 포인팅 허용'"
            :value.sync="pointing"
          ></r-check>
        </div>
      </template>

      <p class="rec-setting__header">로컬 녹화 설정</p>

      <div class="rec-setting__row">
        <p class="rec-setting__text">녹화대상</p>
        <div class="rec-setting__selector">
          <r-radio
            :options="localRecordTarget"
            :value="'value'"
            :text="'text'"
            :selectedOption.sync="recordTarget"
          ></r-radio>
        </div>
      </div>
      <div class="rec-setting__row">
        <p class="rec-setting__text">최대 녹화 시간</p>
        <r-select
          class="rec-setting__selector"
          @changeValue="changeSetting('time', $event)"
          :options="localRecTimeOpt"
          :value="'value'"
          :text="'text'"
          :defaultValue="localRecord.time"
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
            popperClass="rec-setting__custom-popover"
            width="25.4286rem"
          >
            <img
              slot="reference"
              class="rec-setting--tooltip-icon"
              src="~assets/image/ic_tool_tip.svg"
            />
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
          @changeValue="changeSetting('interval', $event)"
          :options="localRecIntervalOpt"
          :value="'value'"
          :text="'text'"
          :defaultValue="localRecord.interval"
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
            popperClass="rec-setting__custom-popover"
            width="25.4286rem"
          >
            <img
              slot="reference"
              class="rec-setting--tooltip-icon"
              src="~assets/image/ic_tool_tip.svg"
            />
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
          @changeValue="changeSetting('resolution', $event)"
          :options="localRecResOpt"
          :value="'value'"
          :text="'text'"
          :defaultValue="localRecord.resolution"
        >
        </r-select>
      </div>
      <div class="rec-setting__row checkbox" v-if="isLeader">
        <p class="rec-setting__text">참가자 로컬 녹화</p>
        <r-check
          :text="'참가자 로컬 녹화 허용'"
          :value.sync="localRecording"
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
import { ROLE, CONTROL } from 'configs/remote.config'
import {
  localRecTimeOpt,
  localRecResOpt,
  localRecIntervalOpt,
  localRecordTarget,
  RECORD_TARGET,
} from 'utils/recordOptions'

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
      localRecording: false,
      pointing: false,

      visibleFlag: false,
      toastFlag: false,

      recordTarget: RECORD_TARGET.WORKER,

      localRecTimeOpt: localRecTimeOpt,
      localRecResOpt: localRecResOpt,
      localRecIntervalOpt: localRecIntervalOpt,
      localRecordTarget: localRecordTarget,
    }
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
  },

  computed: {
    ...mapGetters(['localRecord', 'allow', 'screenStream']),
    isLeader() {
      if (this.account.roleType === ROLE.EXPERT_LEADER) {
        return true
      } else {
        return false
      }
    },
  },

  watch: {
    visible(flag) {
      this.visibleFlag = flag
    },
    localRecording(flag) {
      this.setAllow({
        localRecording: !!flag,
      })
      this.$call.control(CONTROL.LOCAL_RECORD, !!flag)
      this.$localStorage.setAllow('localRecording', !!flag)
      this.showToast()
    },
    pointing(flag) {
      this.setAllow({
        pointing: !!flag,
      })
      this.$call.control(CONTROL.POINTING, !!flag)
      this.$localStorage.setAllow('pointing', !!flag)
      this.showToast()
    },

    recordTarget(target) {
      console.log(target)
      switch (target) {
        case RECORD_TARGET.WORKER:
          //set worker stream(main view + participants)
          this.setLocalRecordTarget(target)

          //don't need screen stream when record worker.
          this.setScreenStream(null)
          this.showToast()
          break
        case RECORD_TARGET.SCREEN:
          //set screen stream for local record
          this.setLocalRecordTarget(target)
          this.showToast()
          break
        default:
          console.log('unknown recordTarget ::', target)
          break
      }
    },
  },
  methods: {
    ...mapActions([
      'setRecord',
      'setAllow',
      'setScreenStream',
      'setLocalRecordTarget',
    ]),
    changeSetting(item, setting) {
      const param = {}
      param[item] = setting.value
      this.setRecord(param)
      this.$localStorage.setRecord(item, setting.value)
      this.showToast()
    },

    beforeClose() {
      this.$emit('update:visible', false)
    },

    showToast() {
      if (this.toastFlag) {
        this.toastNotify('변경사항을 저장했습니다.')
      }
    },
  },

  created() {
    this.localRecording = this.allow.localRecording
    this.pointing = this.allow.pointing
  },
  mounted() {
    this.$nextTick(() => {
      this.toastFlag = true
    })
  },
}
</script>
