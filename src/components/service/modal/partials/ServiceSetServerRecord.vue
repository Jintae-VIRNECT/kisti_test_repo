<template>
  <section class="service-setting__view">
    <p class="service-setting--header" :class="{ disable: isServerRecording }">
      {{ isMobileSize ? '' : $t('service.setting_server_record') }}
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
        :targetElement="'.service-mobile-setting-modal'"
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
        :targetElement="'.service-mobile-setting-modal'"
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

<style lang="scss">
@import '~assets/style/mixin';

@include responsive-mobile {
  .service-setting__view {
    width: 100%;
    padding: 2rem 1.6rem;
    .service-setting--header {
      margin-bottom: 0px;
    }

    .service-setting__row {
      display: flex;
      flex-direction: column;
      align-items: flex-start;

      .service-setting__text {
        @include fontLevel(100);
        margin-bottom: 0.8rem;
        color: $new_color_text_sub;
      }
      .service-setting__selector {
        width: 100%;
      }
      .service-setting__selector > .select-label {
        width: 100%;
        height: 4rem;
        @include fontLevel(100);
        color: $new_color_text_main;
        &::after {
          top: 50%;
          width: 2rem;
          height: 2rem;
          transform: translateY(-50%);
        }
      }
    }

    .service-setting__row:nth-child(2) {
      margin-bottom: 1.6rem;
    }
  }
}
</style>
