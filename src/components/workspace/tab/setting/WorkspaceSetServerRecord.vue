<template>
  <section class="setting-section">
    <div class="setting-section__title">
      {{ $t('workspace.setting_server_record') }}
    </div>
    <div class="setting-section__body horizon">
      <figure class="setting__figure">
        <p class="setting__label">
          {{ $t('workspace.setting_record_maxtime') }}
        </p>
        <r-select
          class="setting__r-selecter"
          :options="serverRecTime"
          value="value"
          text="text"
          :selectedValue.sync="recordTime"
        >
        </r-select>
      </figure>
      <figure class="setting__figure">
        <div class="setting__figure--wrapper">
          <p class="setting__label">
            {{ $t('workspace.setting_record_resolution') }}
          </p>
        </div>
        <r-select
          class="setting__r-selecter"
          :options="serverRecResOpt"
          value="value"
          text="text"
          :selectedValue.sync="recordResolution"
        >
        </r-select>
      </figure>
    </div>
    <p class="setting__label dot">
      {{ $t('workspace.setting_record_setting_description') }}
    </p>
  </section>
</template>
<script>
import RSelect from 'RemoteSelect'
import { mapGetters, mapActions } from 'vuex'
import { serverRecTime, serverRecResOpt } from 'utils/recordOptions'
export default {
  components: {
    RSelect,
  },
  data() {
    return {
      recordTime: '',
      recordInterval: '',
      serverRecResOpt: serverRecResOpt,
      recordResolution: '',
    }
  },
  computed: {
    ...mapGetters(['serverRecord']),
    serverRecTime() {
      const options = serverRecTime.map(time => {
        return {
          value: time,
          text: `${time} ${this.$t('date.minute')}`,
        }
      })
      return options
    },
  },
  watch: {
    recordTime(time) {
      this.setServerRecLength(time)
    },
    recordResolution(resolution) {
      this.setRecResolution(resolution)
    },
  },
  methods: {
    ...mapActions(['setServerRecord']),
    setServerRecLength(time) {
      this.setServerRecord({
        time: time,
      })
      window.myStorage.setItemPiece('serverRecordInfo', 'time', time)
    },
    setRecResolution(resolution) {
      this.setServerRecord({
        resolution: resolution,
      })
      window.myStorage.setItemPiece(
        'serverRecordInfo',
        'resolution',
        resolution,
      )
    },
  },
  created() {
    if (this.serverRecord.time) {
      this.recordTime = this.serverRecord.time
    }
    if (this.serverRecord.resolution) {
      this.recordResolution = this.serverRecord.resolution
    }
  },
}
</script>
