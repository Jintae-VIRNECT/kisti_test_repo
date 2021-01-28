<template>
  <modal
    :visible.sync="visibleFlag"
    :showClose="true"
    width="auto"
    :beforeClose="beforeClose"
    class="service-setting-modal"
    :title="$t('환경 설정')"
  >
    <div class="service-setting">
      <section class="service-setting-nav">
        <button
          class="service-setting-nav__menu"
          :class="{ active: tabview === 'group' }"
          :data-text="$t('service.setting_pointing')"
          @click="tabChange('pointing')"
        >
          {{ $t('service.setting_pointing') }}
        </button>
        <button
          class="service-setting-nav__menu"
          :class="{ active: tabview === 'local-record' }"
          :data-text="$t('service.setting_local_record')"
          @click="tabChange('local-record')"
        >
          {{ $t('service.setting_local_record') }}
        </button>
        <button
          class="service-setting-nav__menu"
          :class="{ active: tabview === 'server-record' }"
          :data-text="$t('service.setting_server_record')"
          @click="tabChange('server-record')"
        >
          {{ $t('service.setting_server_record') }}
        </button>
        <button
          class="service-setting-nav__menu"
          :class="{ active: tabview === 'translate' }"
          :data-text="$t('service.setting_translate')"
          @click="tabChange('translate')"
        >
          {{ $t('service.setting_translate') }}
        </button>
      </section>

      <!-- 포인팅 리더 only -->
      <template v-if="tabview === 'pointing'">
        <template v-if="isLeader">
          <p class="service-setting--header">
            {{ $t('service.setting_pointing') }}
          </p>
          <div class="service-setting__row">
            <p class="service-setting__text">
              {{ $t('service.setting_pointing_participant') }}
            </p>

            <r-check
              :text="$t('service.setting_pointing_allow')"
              :value.sync="pointing"
            ></r-check>
          </div>
        </template>
      </template>

      <!-- 로컬 녹화 / 로컬녹화중에는 사용 불가능해야함.-->
      <template v-if="tabview === 'local-record'">
        <p
          class="service-setting--header"
          :class="{ disable: isLocalRecording }"
        >
          {{ $t('service.setting_local_record') }}
          <span v-if="isLocalRecording" class="service-setting--warning">
            {{ $t('service.setting_local_record_warning') }}
          </span>
        </p>

        <div
          class="service-setting__row"
          v-if="!isTablet"
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

        <!-- 사파리인 경우 로컬녹화 설정의 녹화대상(영상 녹화, 화면녹화), 참가자 로컬 녹화 여부만 출력됨 -->
        <template v-if="!isSafari">
          <div
            class="service-setting__row"
            :class="{ disable: isLocalRecording }"
          >
            <p class="service-setting__text">
              {{ $t('service.setting_record_max_time') }}
            </p>
            <r-select
              class="service-setting__selector"
              :options="localRecTimeOpt"
              value="value"
              text="text"
              :selectedValue.sync="maxRecordTime"
            >
            </r-select>
          </div>
          <div
            class="service-setting__row"
            :class="{ disable: isLocalRecording }"
          >
            <div class="service-setting__text custom">
              <p>
                {{ $t('service.setting_record_interval') }}
              </p>
              <tooltip
                customClass="tooltip-guide"
                :content="$t('service.setting_record_time_tooltip')"
                :placement="isTablet ? 'bottom' : 'right'"
                effect="blue"
                :guide="true"
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
            >
            </r-select>
          </div>

          <div
            class="service-setting__row"
            :class="{ disable: isLocalRecording }"
          >
            <div class="service-setting__text custom">
              <p>
                {{ $t('service.setting_record_resolution') }}
              </p>
              <tooltip
                customClass="tooltip-guide"
                :content="$t('service.setting_record_resolution_tooltip')"
                :placement="isTablet ? 'bottom' : 'right'"
                effect="blue"
                :guide="true"
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
            >
            </r-select>
          </div>
        </template>
        <div
          class="service-setting__row"
          v-if="isLeader"
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
      </template>

      <!-- 서버 녹화 관련 옵션 -->
      <template v-if="tabview === 'server-record'">
        <template v-if="isLeader && useRecording">
          <p
            class="service-setting--header"
            :class="{ disable: isServerRecording }"
          >
            {{ $t('service.setting_server_record') }}
            <span v-if="isServerRecording" class="service-setting--warning">
              {{ $t('service.setting_server_record_warning') }}
            </span>
          </p>
          <div
            class="service-setting__row"
            :class="{ disable: isServerRecording }"
          >
            <p class="service-setting__text">
              {{ $t('service.setting_record_max_time') }}
            </p>
            <r-select
              class="service-setting__selector"
              :options="serverRecTime"
              value="value"
              text="text"
              :selectedValue.sync="serverMaxRecordTime"
            >
            </r-select>
          </div>

          <div
            class="service-setting__row"
            :class="{ disable: isServerRecording }"
          >
            <div class="service-setting__text custom">
              <p>
                {{ $t('service.setting_record_resolution') }}
              </p>
            </div>

            <r-select
              class="service-setting__selector"
              :options="serverRecResOpt"
              value="value"
              text="text"
              :selectedValue.sync="serverRecordResolution"
            >
            </r-select>
          </div>
        </template>
      </template>

      <!-- 번역 관련 기능 (전체 기능 포함 필요함)-->
      <template v-if="tabview === 'translate'">
        <template v-if="useTranslate">
          <p class="service-setting--header">
            {{ $t('service.setting_translate') }}
          </p>
          <div class="service-setting__row">
            <p class="service-setting__text">
              {{ $t('service.setting_translate_use') }}
            </p>
            <r-check
              :text="$t('service.setting_translate_use_allow')"
              :value.sync="useTranslateAllow"
            ></r-check>
          </div>
          <div class="service-setting__row">
            <div class="service-setting__text custom">
              <p>{{ $t('service.setting_translate_language') }}</p>
              <tooltip
                customClass="tooltip-guide"
                :content="$t('service.setting_translate_language_tooltip')"
                :placement="isTablet ? 'bottom' : 'right'"
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
              class="service-setting__selector"
              :options="languageCodes"
              value="code"
              text="text"
              :disabled="!useTranslateAllow"
              :selectedValue.sync="translateCode"
            >
            </r-select>
          </div>
        </template>
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

import { mapGetters, mapActions } from 'vuex'
import { ROLE, CONTROL } from 'configs/remote.config'
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

      tabview: 'pointing',
    }
  },

  computed: {
    ...mapGetters([
      'view',
      'serverRecord',
      'serverRecordStatus',
      'localRecord',
      'localRecordStatus',
      'allowLocalRecord',
      'allowPointing',
      'translate',
      'useTranslate',
      'languageCodes',
      'modalSetting',
      'useRecording',
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
      return this.account.roleType === ROLE.LEADER
    },
    isLocalRecording() {
      return this.localRecordStatus === 'START'
    },
    isServerRecording() {
      return this.serverRecordStatus !== 'STOP'
    },
  },

  watch: {
    modalSetting(flag) {
      this.visibleFlag = flag
    },
    localRecording(flag) {
      if (this.initing === false) return
      this.$call.sendControl(CONTROL.LOCAL_RECORD, !!flag)
      // this.$localStorage.setAllow('localRecord', !!flag)
    },
    pointing(flag) {
      if (this.initing === false) return
      this.$call.sendControl(CONTROL.POINTING, !!flag)
      // this.$localStorage.setAllow('pointing', !!flag)
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
      'setLocalRecordTarget',
      'setTranslate',
      'showModalSetting',
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
      this.showModalSetting(false)
    },

    showToast() {
      this.toastNotify(this.$t('service.setting_save'))
    },
    init() {
      this.initing = false
      this.translateCode = this.translate.code
      this.useTranslateAllow = this.translate.flag
      this.maxRecordTime = this.localRecord.time
      this.maxRecordInterval = this.localRecord.interval
      this.recordResolution = this.localRecord.resolution
      if (this.account.roleType === ROLE.LEADER) {
        this.localRecording = this.allowLocalRecord
        this.pointing = this.allowPointing
      }
      if (this.useRecording) {
        this.serverMaxRecordTime = this.serverRecord.time
        this.serverRecordResolution = this.serverRecord.resolution
      }
      this.$nextTick(() => {
        this.initing = true
      })
    },
    tabChange(view) {
      this.tabview = view
    },
  },

  created() {
    this.init()
  },
}
</script>
