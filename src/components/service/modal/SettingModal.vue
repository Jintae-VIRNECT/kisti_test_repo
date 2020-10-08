<template>
  <modal
    :visible.sync="visibleFlag"
    :showClose="true"
    width="auto"
    :beforeClose="beforeClose"
    class="local-recsetting-modal"
  >
    <div class="rec-setting">
      <template v-if="isLeader">
        <p class="rec-setting--header">{{ $t('service.setting_pointing') }}</p>
        <div class="rec-setting__row">
          <p class="rec-setting__text">
            {{ $t('service.setting_pointing_participant') }}
          </p>

          <r-check
            :text="$t('service.setting_pointing_allow')"
            :value.sync="pointing"
          ></r-check>
        </div>
      </template>

      <p class="rec-setting--header" :class="{ disable: recording }">
        {{ $t('service.setting_local_record') }}
      </p>
      <p v-if="recording" class="rec-setting--warning">
        {{ $t('service.setting_local_record_warning') }}
      </p>

      <div class="rec-setting__row" :class="{ disable: recording }">
        <p class="rec-setting__text">
          {{ $t('service.setting_record_target') }}
        </p>
        <div class="rec-setting__selector">
          <r-radio
            :options="localRecordTargetOpt"
            value="value"
            text="text"
            :selectedOption.sync="recordTarget"
          ></r-radio>
        </div>
      </div>
      <div class="rec-setting__row" :class="{ disable: recording }">
        <p class="rec-setting__text">
          {{ $t('service.setting_record_max_time') }}
        </p>
        <r-select
          class="rec-setting__selector"
          :options="localRecTimeOpt"
          value="value"
          text="text"
          :selectedValue.sync="maxRecordTime"
        >
        </r-select>
      </div>
      <div class="rec-setting__row" :class="{ disable: recording }">
        <div class="rec-setting__text custom">
          <p>
            {{ $t('service.setting_record_interval') }}
          </p>
          <tooltip
            customClass="tooltip-guide"
            :content="$t('service.setting_record_time_tooltip')"
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
          :options="localRecIntervalOpt"
          value="value"
          text="text"
          :selectedValue.sync="maxRecordInterval"
        >
        </r-select>
      </div>

      <div class="rec-setting__row" :class="{ disable: recording }">
        <div class="rec-setting__text custom">
          <p>
            {{ $t('service.setting_record_resolution') }}
          </p>
          <tooltip
            customClass="tooltip-guide"
            :content="$t('service.setting_record_resolution_tooltip')"
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
          :options="localRecResOpt"
          value="value"
          text="text"
          :selectedValue.sync="recordResolution"
        >
        </r-select>
      </div>
      <div
        class="rec-setting__row"
        v-if="isLeader"
        :class="{ disable: recording }"
      >
        <p class="rec-setting__text">
          {{ $t('service.setting_local_record_participant') }}
        </p>
        <r-check
          :text="$t('service.setting_local_record_allow')"
          :value.sync="localRecording"
        ></r-check>
      </div>
      <template v-if="useTranslate">
        <p class="rec-setting--header">
          {{ '번역 설정' }}
        </p>
        <div class="rec-setting__row">
          <p class="rec-setting__text">
            {{ '번역 사용' }}
          </p>
          <r-check
            :text="'번역 사용 허용'"
            :value.sync="useTranslateAllow"
          ></r-check>
        </div>
        <div class="rec-setting__row">
          <div class="rec-setting__text custom">
            <p>{{ '번역 언어 설정' }}</p>
            <tooltip
              customClass="tooltip-guide"
              :content="'채팅 시, 번역 받을 국가언어를 설정해주세요.'"
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
            :options="translateOption"
            value="code"
            text="name"
            :disabled="!useTranslateAllow"
            :selectedValue.sync="translateCode"
          >
          </r-select>
        </div>
      </template>
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

import { languageCode } from 'utils/translate'

import { mapGetters, mapActions } from 'vuex'
import { ROLE, CONTROL } from 'configs/remote.config'
import { USE_TRANSLATE } from 'configs/env.config'
import {
  localRecTime,
  localRecResOpt,
  localRecInterval,
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

      recordTarget: this.$store.state.settings.localRecordTarget,

      localRecResOpt: localRecResOpt,
      maxRecordTime: '',
      maxRecordInterval: '',
      recordResolution: '',
      useTranslateAllow: false,
      translateCode: 'ko',
      translateOption: languageCode,
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
      'translate',
    ]),
    localRecTimeOpt() {
      const options = localRecTime.map(time => {
        return {
          value: time,
          text: `${time} ${this.$t('date.minute')}`,
        }
      })
      return options
    },
    localRecIntervalOpt() {
      const options = localRecInterval.map(interval => {
        return {
          value: interval,
          text: `${interval} ${this.$t('date.minute')}`,
        }
      })
      return options
    },
    localRecordTargetOpt() {
      return [
        {
          text: this.$t('service.setting_record_stream'),
          value: RECORD_TARGET.WORKER,
        },
        {
          text: this.$t('service.setting_record_screen'),
          value: RECORD_TARGET.SCREEN,
        },
      ]
    },
    isLeader() {
      if (this.account.roleType === ROLE.LEADER) {
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
    useTranslate() {
      return USE_TRANSLATE
    },
  },

  watch: {
    visible(flag) {
      this.visibleFlag = flag
    },
    localRecording(flag) {
      if (!this.isCurrentView) return
      this.$call.control(CONTROL.LOCAL_RECORD, !!flag)
      this.$localStorage.setAllow('localRecord', !!flag)
    },
    pointing(flag) {
      if (!this.isCurrentView) return
      this.$call.control(CONTROL.POINTING, !!flag)
      this.$localStorage.setAllow('pointing', !!flag)
    },
    allowLocalRecord(val, bVal) {
      if (!this.isCurrentView) return
      if (val !== bVal) {
        if (val === true) {
          this.addChat({
            status: 'record-allow',
            type: 'system',
          })
        } else {
          this.addChat({
            status: 'record-not-allow',
            type: 'system',
          })
        }
      }
    },
    allowPointing(val, bVal) {
      if (!this.isCurrentView) return
      if (val !== bVal) {
        if (val === true) {
          this.addChat({
            status: 'pointing-allow',
            type: 'system',
          })
        } else {
          this.addChat({
            status: 'pointing-not-allow',
            type: 'system',
          })
        }
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
    maxRecordTime(time) {
      this.changeSetting('time', time)
    },
    maxRecordInterval(interval) {
      this.changeSetting('interval', interval)
    },
    recordResolution(resolution) {
      this.changeSetting('resolution', resolution)
    },
    translateCode(code) {
      this.changeTranslate('code', code)
    },
    useTranslateAllow(flag) {
      this.changeTranslate('flag', flag)
    },
  },
  methods: {
    ...mapActions([
      'setRecord',
      'setScreenStream',
      'setLocalRecordTarget',
      'addChat',
      'setTranslate',
    ]),
    changeSetting(item, setting) {
      const param = {}
      param[item] = setting
      this.setRecord(param)
      this.$localStorage.setRecord(item, setting)
      // this.showToast()
    },
    changeTranslate(item, setting) {
      const param = {}
      param[item] = setting
      this.setTranslate(param)
      this.$localStorage.setTranslate(item, setting)
      // this.showToast()
    },

    beforeClose() {
      this.$emit('update:visible', false)
    },

    showToast() {
      this.toastNotify(this.$t('service.setting_save'))
    },
  },

  created() {
    this.translateCode = this.translate.code
    this.useTranslateAllow = this.translate.flag
    this.maxRecordTime = this.localRecord.time
    this.maxRecordInterval = this.localRecord.interval
    this.recordResolution = this.localRecord.resolution
    if (this.account.roleType === ROLE.LEADER) {
      this.localRecording = this.allowLocalRecord
      this.pointing = this.allowPointing
    }
  },
}
</script>
