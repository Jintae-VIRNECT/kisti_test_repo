<template>
  <div class="datepicker">
    <vue-datepicker
      class="custom-date-picker"
      :format="format"
      :pickerName="pickerName"
      :highlighted="highlighted"
      :useUtc="true"
      :minimumView="minimumView"
      :maximumView="maximumView"
    >
    </vue-datepicker>
    <button @click="toggleCalendar" class="calendar-button">
      <img v-if="!isActive" src="~assets/image/ic_calendar_default.svg" />
      <img v-else src="~assets/image/ic_calendar_active.svg" />
    </button>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
  name: 'Datepicker',
  data() {
    return {
      isActive: false,
    }
  },
  props: {
    pickerName: {
      type: String,
      default: '',
      required: true,
    },
    minimumView: {
      type: String,
      default: 'day',
    },
    maximumView: {
      type: String,
      default: 'year',
    },
    highlighted: {
      type: Object,
    },
    format: {
      type: String,
      default: 'yyyy-MM-dd',
    },
  },
  computed: {
    ...mapGetters(['calendars']),
    calendar() {
      const index = this.calendars.findIndex(
        cal => cal.name === this.pickerName,
      )
      if (index < 0) return {}
      return this.calendars[index]
    },
  },
  watch: {
    calendar: {
      handler() {
        if (typeof this.calendar.status === 'boolean') {
          this.isActive = this.calendar.status
        }
      },
      deep: true,
    },
  },
  methods: {
    toggleCalendar() {
      this.$eventBus.$emit('toggle::calendar', this.pickerName)
    },
  },
  mounted() {},
}
</script>
<style lang="scss">
.datepicker {
  display: flex;

  width: 185px;
  height: 42px;
}
.custom-date-picker {
  &.vdp-datepicker {
    width: 143px;
    height: 42px;
    .vdp-wrapper {
      // width: 143px;
      height: 42px;
    }

    .vdp-datepicker--input {
      position: relative;
      width: 143px;
      padding: 10.0002px 0 14px 20.0004px;
      border-right: none;
      border-radius: 4px 0px 0px 4px;
    }
    .vdp-datepicker__calendar {
      top: 43px;
      right: -42px;
    }
  }
}

.calendar-button {
  width: 42px;
  height: 42px;
  padding-top: 3px;
  background: rgb(255, 255, 255);
  border: 1px solid rgb(227, 227, 227);
  border-radius: 0px 4px 4px 0px;
}
</style>
