<template>
  <button class="calendar-button">
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
  },
}
</script>

<style lang="scss">
.calendar-button {
  width: 48px;
  height: 48px;
  background: rgb(255, 255, 255);
  border: 1px solid rgb(229, 231, 233);
  border-radius: 0px;
}
</style>
