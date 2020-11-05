<template>
  <section class="setting-section">
    <div class="setting-section__title">
      서버 녹화 설정
    </div>
    <p class="setting__label dot">
      수정된 설정 값은 녹화를 시작하는 시점에 적용이 됩니다.
    </p>

    <div class="setting-section__body horizon first">
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
    </div>
  </section>
</template>
<script>
import RSelect from 'RemoteSelect'
import { mapGetters, mapActions } from 'vuex'
import { serverRecTime } from 'utils/recordOptions'
export default {
  data() {
    return {
      recordTime: '',
      recordInterval: '',
    }
  },
  components: {
    RSelect,
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
  },
  methods: {
    ...mapActions(['setServerRecord']),
    setServerRecLength(time) {
      this.setServerRecord({
        time: time,
      })
      this.$localStorage.setServerRecord('time', time)
    },
  },
  created() {
    if (this.serverRecord.time) {
      this.recordTime = this.serverRecord.time
    }
  },
}
</script>
