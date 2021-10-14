<template>
  <section class="stand-sit-control-group">
    <button
      :disabled="!sitStandBtn"
      class="toggle-btn"
      :class="{
        active: isSpotStand,
      }"
      @click="setStand(true)"
    >
      Stand
    </button>
    <button
      :disabled="!sitStandBtn"
      class="toggle-btn"
      :class="{
        active: !isSpotStand,
      }"
      @click="setStand(false)"
    >
      Sit
    </button>
  </section>
</template>

<script>
import { mapGetters } from 'vuex'
import Store from 'stores/remote/store'
import { spotControl } from 'plugins/remote/spot/spotSocket'

export default {
  props: {
    sitStandBtn: {
      type: Boolean,
    },
  },
  computed: {
    ...mapGetters(['isSpotStand']),
  },
  methods: {
    setStand(isStand) {
      Store.commit('SET_IS_SPOT_STAND', isStand)
      if (isStand) spotControl.stand()
      else spotControl.sit()
    },
  },
}
</script>

<style lang="scss">
@import '~assets/style/mixin';

.stand-sit-control-group {
  position: absolute;
  bottom: 5.5vh;
  left: 5.5vh;
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  padding: 0.277vh;
  background-color: #2c2c2c;
  border: 1px solid #353535;
  border-radius: 6px;

  .toggle-btn {
    width: 6.487vh;
    height: 6.487vh;
    margin: 3px;
    color: #727272;
    font-weight: 500;
    font-size: 1.481vh;

    font-family: font-family2;
    background-color: transparent;
    border: transparent;

    &.active {
      color: white;
      background-color: rgba(white, 0.3);
      border: 1px solid #979797;
      border-radius: 4px;
    }

    &:disabled {
      opacity: 0.4;
    }
  }
}

@include responsive-tablet {
  .stand-sit-control-group {
    bottom: 5.5vh;
    left: 16.9vw;
    padding: 3px;

    .toggle-btn {
      width: 64px;
      height: 64px;
      @include fontLevel(200);
    }
  }
}

@include responsive-mobile {
  .stand-sit-control-group {
    bottom: 24px;
    left: 16px;
    padding: 3px;

    .toggle-btn {
      width: 5rem;
      height: 5rem;
      @include fontLevel(75);
    }
  }
}
</style>
