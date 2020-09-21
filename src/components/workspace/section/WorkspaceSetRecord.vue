<template>
  <section class="setting-section">
    <div class="setting-section__title">
      {{ $t('workspace.setting_record_choice') }}
    </div>
    <div class="setting-section__body horizon first">
      <figure class="setting__figure">
        <p class="setting__label">
          {{ $t('workspace.setting_record_maxtime') }}
        </p>
        <r-select
          class="setting__r-selecter"
          :options="localRecTimeOpt"
          value="value"
          text="text"
          :selectedValue.sync="recordTime"
        >
        </r-select>
      </figure>

      <figure class="setting__figure">
        <div class="setting__figure--wrapper">
          <p class="setting__label">
            {{ $t('workspace.setting_record_interval') }}
          </p>
          <tooltip
            customClass="tooltip-guide"
            :content="$t('workspace.setting_record_interval_tooltip')"
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
          class="setting__r-selecter"
          :options="localRecIntervalOpt"
          value="value"
          text="text"
          :selectedValue.sync="recordInterval"
        >
        </r-select>
      </figure>
    </div>
  </section>
</template>
<script>
import RSelect from 'RemoteSelect'
import Tooltip from 'Tooltip'
import { mapGetters, mapActions } from 'vuex'
import { localRecTime, localRecInterval } from 'utils/recordOptions'
export default {
  data() {
    return {
      recordTime: '',
      recordInterval: '',
    }
  },
  components: {
    RSelect,
    Tooltip,
  },
  computed: {
    ...mapGetters(['localRecord']),
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
  },
  watch: {
    recordTime(time) {
      this.setRecLength(time)
    },
    recordInterval(interval) {
      this.setRecInterval(interval)
    },
  },
  methods: {
    ...mapActions(['setRecord']),
    setRecLength(time) {
      console.log(time)
      this.setRecord({
        time: time,
      })
      this.$localStorage.setRecord('time', time)
    },
    setRecInterval(newInterval) {
      this.setRecord({
        interval: newInterval.value,
      })
      this.$localStorage.setRecord('interval', newInterval.value)
    },
  },
  mounted() {
    if (this.localRecord.time) {
      this.recordTime = this.localRecord.time
    }
    if (this.localRecord.interval) {
      this.recordInterval = this.localRecord.interval
    }
  },
}
</script>
