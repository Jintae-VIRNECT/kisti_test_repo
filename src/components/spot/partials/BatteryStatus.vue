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
@import '~assets/style/mixin';

.battery-status {
  position: absolute;
  top: 5.5vh;
  right: 5.5vh;
  display: flex;
  align-items: center;
  justify-content: center;

  height: 3.3vh;

  padding: 0 0.714rem;
  background-color: #333333;
  border-radius: 3px;

  .icon {
    width: 2.2vh;
    height: 2.2vh;
    margin-right: 0.714rem;
  }

  span {
    font-weight: 500;
    font-size: 1.481vh;
    font-family: $font_family2;
  }
}

@include responsive-tablet {
  .battery-status {
    top: 2.96vh;
    right: 16.8vw;
    color: #dadada;
    @include fontLevel(200);
  }
}

@include responsive-mobile {
  .battery-status {
    top: 20px;
    right: 16px;
    color: #dadada;
    @include fontLevel(75);
  }
}
</style>
