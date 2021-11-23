<template>
  <button class="self-flash-button" :class="{ on }" @click="onToggle"></button>
</template>

<script>
export default {
  name: 'MobileSelfFlashButton',
  data() {
    return {
      on: false,
    }
  },
  methods: {
    onToggle() {},
  },
}
</script>

<style lang="scss" scoped>
.self-flash-button {
  width: 3.6rem;
  height: 3.6rem;
  background: url(~assets/image/call/mdpi_icon_flash_off_new.svg) center
    no-repeat;

  &.on {
    background: url(~assets/image/call/mdpi_icon_flash_on_new.svg) center
      no-repeat;
  }
}
</style>
