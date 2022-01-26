<template>
  <section class="setting-section">
    <div class="setting-section__title">
      {{ $t('workspace.setting_record_choice') }}
    </div>
    <div class="setting-section__body horizon">
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
            :placement="isMobileDevice ? 'bottom' : 'right'"
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
          class="setting__r-selecter"
          :options="localRecIntervalOpt"
          value="value"
          text="text"
          :selectedValue.sync="recordInterval"
        >
        </r-select>
      </figure>
    </div>
    <div class="setting-section__body">
      <figure class="setting__figure">
        <div class="setting__figure--wrapper">
          <p class="setting__label">
            {{ $t('workspace.setting_record_resolution') }}
          </p>
          <tooltip
            customClass="tooltip-guide"
            :content="$t('workspace.setting_record_resolution_tooltip')"
            :placement="isMobileDevice ? 'bottom' : 'right'"
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
          class="setting__r-selecter"
          :options="localRecResOpt"
          value="value"
          text="text"
          :selectedValue.sync="recordResolution"
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
import {
  localRecTime,
  localRecInterval,
  localRecResOpt,
} from 'utils/recordOptions'
export default {
  data() {
    return {
      recordTime: '',
      recordInterval: '',
      localRecResOpt: localRecResOpt,
      recordResolution: '',
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
    recordResolution(time) {
      this.setRecResolution(time)
    },
    recordTime(time) {
      this.setRecLength(time)
    },
    recordInterval(interval) {
      this.setRecInterval(interval)
    },
  },
  methods: {
    ...mapActions(['setRecord']),
    setRecResolution(resolution) {
      this.setRecord({
        resolution: resolution,
      })
      window.myStorage.setItemPiece('recordInfo', 'resolution', resolution)
    },
    setRecLength(time) {
      this.setRecord({
        time: time,
      })
      window.myStorage.setItemPiece('recordInfo', 'time', time)
    },
    setRecInterval(newInterval) {
      this.setRecord({
        interval: newInterval,
      })
      window.myStorage.setItemPiece('recordInfo', 'interval', newInterval)
    },
  },
  created() {
    if (this.localRecord.time) {
      this.recordTime = this.localRecord.time
    }
    if (this.localRecord.interval) {
      this.recordInterval = this.localRecord.interval
    }
    if (this.localRecord.resolution) {
      this.recordResolution = this.localRecord.resolution
    }
  },
}
</script>
