<template>
  <div class="datepicker">
    <vue-datepicker
      :format="'yyyy-MM-dd'"
      :pickerName="pickerName"
      :highlighted="highlighted"
      :useUtc="true"
    >
    </vue-datepicker>
    <button
      @click="toggleCalendar"
      @blur="toggleCalendar"
      class="calendar-button"
    >
      <img v-if="!isActive" src="~assets/image/ic_calendar_default.svg" />
      <img v-else src="~assets/image/ic_calendar_active.svg" />
    </button>
  </div>
</template>

<script>
export default {
  name: 'Datepicker',
  props: {
    pickerName: {
      type: String,
      default: '',
      required: true,
    },
    highlighted: {
      type: Object,
    },
  },
  methods: {
    toggleCalendar(event) {
      if (event.type === 'blur' && !this.isActive) {
        return
      }
      this.$eventBus.$emit('toggle::calendar', this.pickerName)
    },
  },
  mounted() {},
}
</script>
