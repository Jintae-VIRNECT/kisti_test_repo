<template>
  <button
    @click="toggleCalendar"
    @blur="toggleCalendar"
    class="calendar-button"
  >
    <img v-if="!isActive" src="~@/assets/img/ic_calendar_default.svg" />
    <img v-else src="~@/assets/img/ic_calendar_active.svg" />
  </button>
</template>

<script>
export default {
  name: 'RemoteCalendarButton',
  props: {
    isActive: {
      type: Boolean,
      default: false,
      required: true,
    },
    pickerName: {
      type: String,
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
}
</script>

<style lang="scss">
.calendar-button {
  width: 48px;
  height: 48px;
  background: #ffffff;
  border: 1px solid #e5e7e9;
  border-radius: 0px;
}
</style>
