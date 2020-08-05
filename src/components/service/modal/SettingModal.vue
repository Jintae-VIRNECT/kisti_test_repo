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
        <p class="rec-setting--header">포인팅 설정</p>
        <div class="rec-setting__row underbar">
          <p class="rec-setting__text">참가자 포인팅</p>

          <r-check
            :text="'참가자 포인팅 허용'"
            :value.sync="pointing"
          ></r-check>
        </div>
      </template>

      <div class="rec-setting__row">
        <p class="rec-setting--header" :class="{ disable: recording }">
          로컬 녹화 설정
        </p>
        <p v-if="recording" class="rec-setting--warning">
          *로컬 녹화 중에서는 설정을 변경 할 수 없습니다.
        </p>
      </div>

      <div class="rec-setting__row" :class="{ disable: recording }">
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
      <div class="rec-setting__row" :class="{ disable: recording }">
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
      <div class="rec-setting__row" :class="{ disable: recording }">
        <div class="rec-setting__text custom">
          <p>
            녹화 간격
          </p>
          <tooltip
            customClass="tooltip-guide"
            content="장시간 로컬 녹화 파일 생성 시, PC의 부하 발생할 수 있기 때문에<br>녹화 파일을 시간 간격으로 나눠서 생성합니다."
            placement="right"
            effect="blue"
          >
            <img
              slot="body"
              class="setting__tooltip--icon"
              src="~assets/image/ic_tool_tip.svg"
            />
          </tooltip>
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

      <div class="rec-setting__row" :class="{ disable: recording }">
        <div class="rec-setting__text custom">
          <p>
            녹화 영상 해상도
          </p>
          <tooltip
            customClass="tooltip-guide"
            content="720p(HD)급이상 해상도 설정 시, PC의 성능에 따라 서비스가<br>원활하지 않을 수 있습니다."
            placement="right"
            effect="blue"
          >
            <img
              slot="body"
              class="setting__tooltip--icon"
              src="~assets/image/ic_tool_tip.svg"
            />
          </tooltip>
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
      <div
        class="rec-setting__row checkbox"
        v-if="isLeader"
        :class="{ disable: recording }"
      >
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
import Tooltip from 'Tooltip'

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
  name: 'SettingModal',
  mixins: [toastMixin],
  components: {
    Modal,
    RSelect,
    RCheck,
    RRadio,
    Tooltip,
  },
  data() {
    return {
      localRecording: false,
      pointing: false,

      visibleFlag: false,

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
    recording: {
      type: Boolean,
      default: false,
    },
    viewType: String,
  },

  computed: {
    ...mapGetters([
      'view',
      'localRecord',
      'allowLocalRecord',
      'allowPointing',
      'screenStream',
    ]),
    isLeader() {
      if (this.account.roleType === ROLE.EXPERT_LEADER) {
        return true
      } else {
        return false
      }
    },
    isCurrentView() {
      if (this.viewType === this.view) {
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
      if (!this.isCurrentView) return
      this.setAllow({
        localRecording: !!flag,
      })
      this.$localStorage.setAllow('localRecording', !!flag)
    },
    pointing(flag) {
      if (!this.isCurrentView) return
      this.setAllow({
        pointing: !!flag,
      })
      this.$localStorage.setAllow('pointing', !!flag)
    },
    allowLocalRecord(val, bVal) {
      if (!this.isCurrentView) return
      if (val !== bVal) {
        this.$call.control(CONTROL.LOCAL_RECORD, !!val)
        this.showToast()
      }
    },
    allowPointing(val, bVal) {
      if (!this.isCurrentView) return
      if (val !== bVal) {
        this.$call.control(CONTROL.POINTING, !!val)
        this.showToast()
      }
    },

    recordTarget(target) {
      if (!this.isCurrentView) return
      switch (target) {
        case RECORD_TARGET.WORKER:
          this.setLocalRecordTarget(target)
          this.setScreenStream(null)
          this.showToast()
          break
        case RECORD_TARGET.SCREEN:
          this.setLocalRecordTarget(target)
          this.showToast()
          break
        default:
          console.error('recordTarget :: Unknown local record target', target)
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
      this.toastNotify('변경사항을 저장했습니다.')
    },
  },

  created() {
    this.localRecording = this.allowLocalRecord
    this.pointing = this.allowPointing
  },
}
</script>
