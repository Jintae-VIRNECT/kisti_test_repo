<template>
  <div class="datepicker" @click.stop>
    <vue-datepicker
      class="custom-date-picker"
      :class="{ active: isActive }"
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
      type: [String, Date],
      default: null,
    },
  },
  data() {
    return {
      isActive: false,
      langSet: {
        ko: ko,
        en: en,
      },
    }
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
}
</script>
<style lang="scss">
@import '~assets/style/vars';
.datepicker {
  display: flex;
  width: 13.2143rem;
  height: 3rem;
}
.custom-date-picker {
  &.vdp-datepicker.active {
    input[type='text'] {
      color: $color_text_main;
    }
  }

  &.vdp-datepicker {
    width: 10.2143rem;
    height: 3rem;
    .vdp-wrapper {
      // width: 143px;
      height: 3rem;
    }

    .vdp-datepicker--input {
      position: relative;
      width: 10.2143rem;
      padding: 0px 0px 0px 1.4286rem;
      border-right: none;
      border-radius: 4px 0px 0px 4px;
    }

    .vdp-datepicker__calendar {
      top: 0.004rem;
      right: 11rem;
      // border: 1px solid rgb(229, 231, 233);
      border-radius: 8px;
      box-shadow: 0px 4px 12px 0px rgba(0, 0, 0, 0.2);
    }
  }
}

.calendar-button {
  width: 3rem;
  height: 3rem;
  padding-top: 0.2143rem;
  background: $color_white;
  border: 1px solid $color_border;
  border-radius: 0px 4px 4px 0px;
  > img {
    width: 1.7143rem;
    height: 1.7143rem;
  }
}
</style>
