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
      <template v-if="isOnpremise">
        <p class="rec-setting--header" :class="{ disable: serverRecording }">
          {{ '서버 녹화 설정' }}
        </p>
        <p v-if="serverRecording" class="rec-setting--warning">
          {{ $t('서버 녹화 중에서는 설정을 변경 할 수 없습니다.') }}
        </p>
        <div class="rec-setting__row" :class="{ disable: serverRecording }">
          <p class="rec-setting__text">
            {{ $t('service.setting_record_max_time') }}
          </p>
          <r-select
            class="rec-setting__selector"
            :options="serverRecTime"
            value="value"
            text="text"
            :selectedValue.sync="serverMaxRecordTime"
          >
          </r-select>
        </div>

        <div class="rec-setting__row" :class="{ disable: serverRecording }">
          <div class="rec-setting__text custom">
            <p>
              {{ $t('service.setting_record_resolution') }}
            </p>
          </div>

          <r-select
            class="rec-setting__selector"
            :options="serverRecResOpt"
            value="value"
            text="text"
            :selectedValue.sync="serverRecordResolution"
          >
          </r-select>
        </div>
      </template>
      <!-- <template v-if="useTranslate">
        <p class="rec-setting--header">
          {{ $t('service.setting_translate') }}
        </p>
        <div class="rec-setting__row">
          <p class="rec-setting__text">
            {{ $t('service.setting_translate_use') }}
          </p>
          <r-check
            :text="$t('service.setting_translate_use_allow')"
            :value.sync="useTranslateAllow"
          ></r-check>
        </div>
        <div class="rec-setting__row">
          <div class="rec-setting__text custom">
            <p>{{ $t('service.setting_translate_language') }}</p>
            <tooltip
              customClass="tooltip-guide"
              :content="$t('service.setting_translate_language_tooltip')"
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
            :options="languageCodes"
            value="code"
            text="text"
            :disabled="!useTranslateAllow"
            :selectedValue.sync="translateCode"
          >
          </r-select>
        </div>
      </template> -->
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
import { RUNTIME_ENV, RUNTIME } from 'configs/env.config'
import {
  localRecTime,
  localRecResOpt,
  localRecInterval,
  serverRecTime,
  serverRecResOpt,
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
      initing: false,
      localRecording: false,
      pointing: false,

      visibleFlag: false,

      recordTarget: this.$store.state.settings.localRecordTarget,

      localRecResOpt: localRecResOpt,
      serverRecResOpt: serverRecResOpt,
      maxRecordTime: '',
      maxRecordInterval: '',
      recordResolution: '',
      serverMaxRecordTime: '',
      serverRecordResolution: '',
      useTranslateAllow: false,
      translateCode: 'ko-KR',
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
    serverRecording: {
      type: Boolean,
      default: false,
    },
    viewType: String,
  },

  computed: {
    ...mapGetters([
      'view',
      'serverRecord',
      'localRecord',
      'allowLocalRecord',
      'allowPointing',
      'translate',
      'useTranslate',
      'languageCodes',
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
    serverRecTime() {
      const options = serverRecTime.map(time => {
        return {
          value: time,
          text: `${time} ${this.$t('date.minute')}`,
        }
      })
      return options
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
    isOnpremise() {
      if (RUNTIME_ENV === RUNTIME.ONPREMISE) {
        return true
      } else {
        return false
      }
    },
  },

  watch: {
    isCurrentView(val) {
      if (val === true) {
        this.initing = false
        this.translateCode = this.translate.code
        this.useTranslateAllow = this.translate.flag
        this.maxRecordTime = this.localRecord.time
        this.maxRecordInterval = this.localRecord.interval
        this.recordResolution = this.localRecord.resolution
        this.serverMaxRecordTime = this.serverRecord.time
        this.serverRecordResolution = this.serverRecord.resolution
        if (this.account.roleType === ROLE.LEADER) {
          this.localRecording = this.allowLocalRecord
          this.pointing = this.allowPointing
        }
        this.$nextTick(() => {
          this.initing = true
        })
      }
    },
    visible(flag) {
      this.visibleFlag = flag
    },
    localRecording(flag) {
      if (this.initing === false) return
      if (!this.isCurrentView) return
      this.$call.control(CONTROL.LOCAL_RECORD, !!flag)
      this.$localStorage.setAllow('localRecord', !!flag)
    },
    pointing(flag) {
      if (this.initing === false) return
      if (!this.isCurrentView) return
      this.$call.control(CONTROL.POINTING, !!flag)
      this.$localStorage.setAllow('pointing', !!flag)
    },
    allowLocalRecord(val, bVal) {
      if (this.initing === false) return
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
      if (this.initing === false) return
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
      if (this.initing === false) return
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
    serverMaxRecordTime(time) {
      this.changeServerSetting('time', time)
    },
    serverRecordResolution(resolution) {
      this.changeServerSetting('resolution', resolution)
    },
    // translateCode(code) {
    //   this.changeTranslate('code', code)
    // },
    // useTranslateAllow(flag) {
    //   this.changeTranslate('flag', flag)
    // },
  },
  methods: {
    ...mapActions([
      'setRecord',
      'setServerRecord',
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
    changeServerSetting(item, setting) {
      const param = {}
      param[item] = setting
      this.setServerRecord(param)
      this.$localStorage.setServerRecord(item, setting)
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
    if (this.isOnpremise) {
      this.serverMaxRecordTime = this.serverRecord.time
      this.serverRecordResolution = this.serverRecord.resolution
    }
    this.$nextTick(() => {
      this.initing = true
    })
  },
}
</script>
