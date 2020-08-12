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
        <p class="rec-setting--header">{{ $t('service.setting_pointing') }}</p>
        <div class="rec-setting__row underbar">
          <p class="rec-setting__text">
            {{ $t('service.setting_pointing_participant') }}
          </p>

          <r-check
            :text="$t('service.setting_pointing_allow')"
            :value.sync="pointing"
          ></r-check>
        </div>
      </template>

      <div class="rec-setting__row">
        <p class="rec-setting--header" :class="{ disable: recording }">
          {{ $t('service.setting_local_record') }}
        </p>
        <p v-if="recording" class="rec-setting--warning">
          {{ $t('service.setting_local_record_warning') }}
        </p>
      </div>

      <div class="rec-setting__row" :class="{ disable: recording }">
        <p class="rec-setting__text">
          {{ $t('service.setting_record_target') }}
        </p>
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
        <p class="rec-setting__text">
          {{ $t('service.setting_record_max_time') }}
        </p>
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
        <p class="rec-setting__text">
          {{ $t('service.setting_local_record_participant') }}
        </p>
        <r-check
          :text="$t('service.setting_local_record_allow')"
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

      recordTarget: RECORD_TARGET.WORKER,

      localRecResOpt: localRecResOpt,
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
    localRecTimeOpt() {
      const options = localRecTime.map(time => {
        return {
          value: time,
          text: time + this.$t('date.minute'),
        }
      })
      return options
    },
    localRecIntervalOpt() {
      const options = localRecInterval.map(interval => {
        return {
          value: interval,
          text: interval + this.$t('date.minute'),
        }
      })
      return options
    },
    localRecordTarget() {
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
      this.toastNotify(this.$t('service.setting_save'))
    },
  },

  created() {
    console.log(localRecInterval)
    this.localRecording = this.allowLocalRecord
    this.pointing = this.allowPointing
  },
}
</script>
