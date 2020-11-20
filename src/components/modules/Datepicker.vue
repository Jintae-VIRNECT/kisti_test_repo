<template>
  <div class="datepicker" @click.stop>
    <vue-datepicker
      class="custom-date-picker"
      :format="format"
      :pickerName="pickerName"
      :highlighted="highlighted"
      :useUtc="true"
      :minimumView="minimumView"
      :maximumView="maximumView"
      :value="initValue"
      :language="langObj"
    >
    </vue-datepicker>
    <button @click="toggleCalendar" class="calendar-button">
      <img
        v-if="!isActive"
        src="~assets/image/calendar/ic_calendar_default.svg"
        alt="calendar_hide"
      />
      <img
        v-else
        src="~assets/image/calendar/ic_calendar_active.svg"
        alt="calendar_active"
      />
    </button>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import ko from 'plugins/remote/datepicker/locale/translations/ko'
import en from 'plugins/remote/datepicker/locale/translations/en'
import langMixin from 'mixins/language'

export default {
  name: 'Datepicker',
  mixins: [langMixin],
  data() {
    return {
      isActive: false,
      langSet: {
        ko: ko,
        en: en,
      },
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
    initValue: {
      type: Date,
      default: null,
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
    langObj() {
      return this.langSet[this.currentLanguage]
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

  width: 13.2143rem;
  height: 3rem;
}
.custom-date-picker {
  &.vdp-datepicker {
    width: 10.2143rem;
    height: 3rem;
    .vdp-wrapper {
      // width: 143px;
      height: 42px;
    }

    .vdp-datepicker--input {
      position: relative;
      width: 10.2143rem;
      padding: 0.7143rem 0 1rem 1.4286rem;
      border-right: none;
      border-radius: 4px 0px 0px 4px;
    }
    .vdp-datepicker__calendar {
      top: 3.0714rem;
      right: -3rem;
    }
  }
}

.calendar-button {
  width: 3rem;
  height: 3rem;
  padding-top: 0.2143rem;
  background: #ffffff;
  border: 1px solid #e3e3e3;
  border-radius: 0px 4px 4px 0px;
}
</style>
