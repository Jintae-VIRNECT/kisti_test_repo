<template>
  <section class="setting-section">
    <div v-if="!isMobileSize" class="setting-section__title">
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
        <p v-if="isMobileSize" class="setting__label dot">
          {{ $t('workspace.setting_record_setting_description') }}
        </p>
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
      <p v-if="!isMobileSize" class="setting__label dot">
        {{ $t('workspace.setting_record_setting_description') }}
      </p>
    </div>

    <div v-if="!isMobileSize" class="setting-section__body">
      <figure class="setting__figure">
        <p class="setting__label">
          {{ $t('workspace.setting_auto_server_record') }}
        </p>
        <check
          :text="$t('workspace.setting_use_auto_server_record')"
          :value.sync="useAutoServerRecord"
        ></check>
      </figure>
    </div>
  </section>
</template>
<script>
import RSelect from 'RemoteSelect'
import Check from 'Check'

import { mapGetters, mapActions } from 'vuex'
import { serverRecTime, serverRecResOpt } from 'utils/recordOptions'
export default {
  components: {
    RSelect,
    Check,
  },
  data() {
    return {
      recordTime: '',
      recordInterval: '',
      serverRecResOpt: serverRecResOpt,
      recordResolution: '',
      useAutoServerRecord: false,
    }
  },
  computed: {
    ...mapGetters(['serverRecord', 'autoServerRecord']),
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
    useAutoServerRecord(flag) {
      this.setAutoServerRecord(flag)
      window.myStorage.setItem('autoServerRecord', flag)
    },
  },
  methods: {
    ...mapActions(['setServerRecord', 'setAutoServerRecord']),
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
    if (this.autoServerRecord) {
      this.useAutoServerRecord = this.autoServerRecord
    }
  },
}
</script>
<style lang="scss" scoped>
@import '~assets/style/mixin';

@include responsive-mobile {
  .setting-section {
    padding-bottom: 2.4rem;
  }

  .setting__figure .setting__label.dot {
    @include fontLevel(50);
    margin-top: 0.8rem;
    color: $new_color_text_sub_description;
  }
}
</style>
