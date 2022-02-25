<template>
  <section class="service-setting__view">
    <p class="service-setting--header" :class="{ disable: isLocalRecording }">
      {{ isMobileSize ? '' : $t('service.setting_local_record') }}
      <span v-if="isLocalRecording" class="service-setting--warning">
        {{ $t('service.setting_local_record_warning') }}
      </span>
    </p>

    <div
      class="service-setting__row"
      v-if="useRecordTargetCtrl"
      :class="{ disable: isLocalRecording }"
    >
      <p class="service-setting__text">
        {{ $t('service.setting_record_target') }}
      </p>
      <div class="service-setting__selector">
        <r-radio
          :options="localRecordTargetOpt"
          value="value"
          text="text"
          :selectedOption.sync="recordTarget"
        ></r-radio>
      </div>
    </div>

    <template v-if="useRecordSetting">
      <div class="service-setting__row" :class="{ disable: isLocalRecording }">
        <p class="service-setting__text">
          {{ $t('service.setting_record_max_time') }}
        </p>
        <r-select
          class="service-setting__selector"
          :options="localRecTimeOpt"
          value="value"
          text="text"
          :selectedValue.sync="maxRecordTime"
          :targetElement="'.modal'"
        >
        </r-select>
      </div>
      <div class="service-setting__row" :class="{ disable: isLocalRecording }">
        <div class="service-setting__text custom">
          <p>
            {{ $t('service.setting_record_interval') }}
          </p>
          <tooltip
            customClass="tooltip-guide"
            :content="$t('service.setting_record_time_tooltip')"
            :placement="isMobileDevice ? 'bottom' : 'right'"
            effect="blue"
            guide
          >
            <img
              slot="body"
              class="setting__tooltip--icon"
              src="~assets/image/ic_tool_tip.svg"
            />
          </tooltip>
        </div>
        <r-select
          class="service-setting__selector"
          :options="localRecIntervalOpt"
          value="value"
          text="text"
          :selectedValue.sync="maxRecordInterval"
          :targetElement="'.modal'"
        >
        </r-select>
      </div>

      <div class="service-setting__row" :class="{ disable: isLocalRecording }">
        <div class="service-setting__text custom">
          <p>
            {{ $t('service.setting_record_resolution') }}
          </p>
          <tooltip
            customClass="tooltip-guide"
            :content="$t('service.setting_record_resolution_tooltip')"
            :placement="isMobileDevice ? 'bottom' : 'right'"
            effect="blue"
            guide
          >
            <img
              slot="body"
              class="setting__tooltip--icon"
              src="~assets/image/ic_tool_tip.svg"
            />
          </tooltip>
        </div>

        <r-select
          class="service-setting__selector"
          :options="localRecResOpt"
          value="value"
          text="text"
          :selectedValue.sync="recordResolution"
          :targetElement="'.modal'"
        >
        </r-select>
      </div>
    </template>
    <template v-if="isLeader">
      <div
        class="service-setting__row"
        v-if="!isMobileSize"
        :class="{ disable: isLocalRecording }"
      >
        <p class="service-setting__text">
          {{ $t('service.setting_local_record_participant') }}
        </p>
        <r-check
          :text="$t('service.setting_local_record_allow')"
          :value.sync="localRecording"
        ></r-check>
      </div>
      <figure v-else-if="isMobileSize" class="setting__figure">
        <p class="setting__label">
          {{ $t('service.setting_local_record_participant') }}
        </p>
        <check
          :text="$t('service.setting_local_record_allow')"
          :value.sync="localRecording"
        ></check>
      </figure>
    </template>
  </section>
</template>

<script>
import {
  localRecTime,
  localRecResOpt,
  localRecInterval,
  RECORD_TARGET,
} from 'utils/recordOptions'

import RCheck from 'RemoteCheckBox'
import Check from 'Check'
import RSelect from 'RemoteSelect'
import RRadio from 'RemoteRadio'
import Tooltip from 'Tooltip'

import toastMixin from 'mixins/toast'

import { mapGetters, mapActions } from 'vuex'
import { ROLE, CONTROL } from 'configs/remote.config'
export default {
  name: 'ServiceSetLocalRecord',
  mixins: [toastMixin],
  components: {
    RSelect,
    RCheck,
    Check,
    RRadio,
    Tooltip,
  },
  data() {
    return {
      localRecording: false,
      localRecResOpt: localRecResOpt,
      recordTarget: this.$store.state.settings.localRecordTarget,
      maxRecordTime: '',
      maxRecordInterval: '',
      recordResolution: '',
    }
  },
  computed: {
    ...mapGetters(['localRecord', 'localRecordStatus', 'allowLocalRecord']),

    useRecordTargetCtrl() {
      return !this.isMobileDevice && !this.isSafari
    },

    useRecordSetting() {
      return !this.isSafari && !this.isMobileSize
    },

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
      return this.account.roleType === ROLE.LEADER
    },
    isLocalRecording() {
      return this.localRecordStatus === 'START'
    },
  },
  watch: {
    localRecording(flag) {
      if (this.initing === false) return
      this.$call.sendControl(CONTROL.LOCAL_RECORD, !!flag)
      // window.myStorage.setAllow('localRecord', !!flag)
    },
    recordTarget(target) {
      if (this.initing === false) return
      switch (target) {
        case RECORD_TARGET.WORKER:
          this.setLocalRecordTarget(target)
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
  },
  methods: {
    ...mapActions(['setRecord', 'setLocalRecordTarget']),
    changeSetting(item, setting) {
      const param = {}
      param[item] = setting
      this.setRecord(param)
      window.myStorage.setItemPiece('recordInfo', item, setting)
      // this.showToast()
    },
    showToast() {
      this.toastNotify(this.$t('service.setting_save'))
    },
    init() {
      this.initing = false

      this.maxRecordTime = this.localRecord.time
      this.maxRecordInterval = this.localRecord.interval
      this.recordResolution = this.localRecord.resolution

      if (this.account.roleType === ROLE.LEADER) {
        this.localRecording = this.allowLocalRecord
      }

      this.$nextTick(() => {
        this.initing = true
      })
    },
  },
  created() {
    this.init()
  },
}
</script>

<style lang="scss">
@import '~assets/style/mixin';

@include responsive-mobile {
  .service-setting__view {
    width: 100%;
    padding: 2rem 1.6rem;

    .setting__figure {
      .setting__label {
        @include fontLevel(100);
        margin-bottom: 0.8rem;
      }
      .check {
        @include responsive-check-toggle;
      }
    }
  }
}
</style>
