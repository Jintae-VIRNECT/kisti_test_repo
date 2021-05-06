<template>
  <section class="service-setting__view">
    <p class="service-setting--header" :class="{ disable: isServerRecording }">
      {{ $t('service.setting_server_record') }}
      <span v-if="isServerRecording" class="service-setting--warning">
        {{ $t('service.setting_server_record_warning') }}
      </span>
    </p>
    <div class="service-setting__row" :class="{ disable: isServerRecording }">
      <p class="service-setting__text">
        {{ $t('service.setting_record_max_time') }}
      </p>
      <r-select
        class="service-setting__selector"
        :options="serverRecTime"
        value="value"
        text="text"
        :selectedValue.sync="serverMaxRecordTime"
        :targetElement="'.modal'"
      >
      </r-select>
    </div>

    <div class="service-setting__row" :class="{ disable: isServerRecording }">
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
        :targetElement="'.modal'"
      >
      </r-select>
    </div>
  </section>
</template>

<script>
import RSelect from 'RemoteSelect'
import { serverRecTime, serverRecResOpt } from 'utils/recordOptions'
import { mapGetters, mapActions } from 'vuex'
export default {
  name: 'ServiceSetServerRecord',
  components: {
    RSelect,
  },
  data() {
    return {
      serverRecResOpt: serverRecResOpt,
      serverMaxRecordTime: '',
      serverRecordResolution: '',
    }
  },
  computed: {
    ...mapGetters(['serverRecord', 'serverRecordStatus', 'useRecording']),

    serverRecTime() {
      const options = serverRecTime.map(time => {
        return {
          value: time,
          text: `${time} ${this.$t('date.minute')}`,
        }
      })
      return options
    },
    isServerRecording() {
      return this.serverRecordStatus !== 'STOP'
    },
  },
  watch: {
    serverMaxRecordTime(time) {
      this.changeServerSetting('time', time)
    },
    serverRecordResolution(resolution) {
      this.changeServerSetting('resolution', resolution)
    },
  },
  methods: {
    ...mapActions(['setRecord', 'setServerRecord']),
    changeServerSetting(item, setting) {
      const param = {}
      param[item] = setting
      this.setServerRecord(param)
      window.myStorage.setItemPiece('serverRecordInfo', item, setting)
      // this.showToast()
    },
    init() {
      if (this.useRecording) {
        this.serverMaxRecordTime = this.serverRecord.time
        this.serverRecordResolution = this.serverRecord.resolution
      }
    },
  },
  created() {
    this.init()
  },
}
</script>
