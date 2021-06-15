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
    },
  },
}
</script>

<style lang="scss">
@import '~assets/style/vars';

.stand-sit-control-group {
  position: absolute;
  bottom: 5.5vh;
  left: 5.5vh;

  padding: 0.277vh;

  background-color: #2c2c2c;
  border: 1px solid #353535;
  border-radius: 6px;

  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  .toggle-btn {
    width: 6.487vh;
    height: 6.487vh;
    margin: 3px;
    background-color: transparent;
    border: transparent;

    font-family: font-family2;
    font-size: 1.481vh;
    font-weight: 500;
    color: #727272;

    &.active {
      background-color: #74747a;
      border: 1px solid #979797;
      border-radius: 4px;
      color: white;
    }

    &:disabled {
      opacity: 0.4;
    }
  }
}
</style>
