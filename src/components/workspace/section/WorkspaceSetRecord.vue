<template>
  <section class="setting-section">
    <div class="setting__title">
      {{ $t('workspace.setting_record_choice') }}
    </div>
    <div class="setting-horizon-wrapper">
      <figure class="setting__figure">
        <p class="setting__label">
          {{ $t('workspace.setting_record_maxtime') }}
        </p>
        <r-select
          class="setting__r-selecter"
          @changeValue="setRecLength"
          :options="localRecTimeOpt"
          :value="'value'"
          :text="'text'"
          :defaultValue="localRecord.time"
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
          @changeValue="setRecInterval"
          :options="localRecIntervalOpt"
          :value="'value'"
          :text="'text'"
          :defaultValue="localRecord.interval"
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
    return {}
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
  },
  methods: {
    ...mapActions(['setRecord']),
    setRecLength(newRecLength) {
      this.setRecord({
        time: newRecLength.value,
      })
      this.$localStorage.setRecord('time', newRecLength.value)
    },
    setRecInterval(newInterval) {
      this.setRecord({
        interval: newInterval.value,
      })
      this.$localStorage.setRecord('interval', newInterval.value)
    },
  },
}
</script>
