<template>
  <section class="battery-status">
    <img
      class="icon"
      :src="require('assets/image/spot/ic_battery.svg')"
      alt=""
    />
    <span>{{ battery ? `${battery}%` : 'Loading...' }}</span>
  </section>
</template>

<script>
export default {
  props: {
    battery: {
      type: Number,
      default: null,
    },
  },
}
</script>

<style lang="scss">
@import '~assets/style/vars';

.battery-status {
  position: absolute;
  top: 5.5vh;
  right: 5.5vh;

  height: 3.3vh;
  display: flex;
  justify-content: center;
  align-items: center;

  padding: 0 0.714rem;
  background-color: #333333;
  border-radius: 3px;

  .icon {
    width: 2.2vh;
    height: 2.2vh;
    margin-right: 0.714rem;
  }

  span {
    font-family: $font_family2;
    font-weight: 500;
    font-size: 1.481vh;
  }
}
</style>
