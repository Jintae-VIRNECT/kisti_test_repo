<template>
  <section class="main-control-group">
    <control-btn
      class="estop"
      :disabled="!estopPossible"
      :class="{
        active: motorBtn && !motorHighlight && estopHighlight && estopBtn,
        inactive: motorBtn && motorHighlight && estopHighlight && !estopBtn,
      }"
      :imgSrc="require('assets/image/spot/ic_estop.svg')"
      @click="estopClick"
    ></control-btn>
    <control-btn
      :disabled="!motorPossible"
      class="motor inactive"
      :class="{
        inactive: !motorBtn,
        possible: motorBtn,
        active: motorBtn && motorHighlight && !isSpotStand,
        'active-disabled': motorBtn && motorHighlight && isSpotStand,
      }"
      :imgSrc="require('assets/image/spot/ic_motor.svg')"
      @click="motorClick"
    ></control-btn>
    <control-btn
      class="fullscreen"
      :imgSrc="require('assets/image/spot/mdpi_icn_Fullscreen_on.svg')"
      @click="setSpotFullscreen"
    ></control-btn>
  </section>
</template>

<script>
import ControlBtn from '../modules/ControlBtn'
import Store from 'stores/remote/store'

import { spotControl } from 'plugins/remote/spot/spotSocket'
import { ESTOP_STATE, MOTOR_POWER } from 'configs/spot.config.js'

import { mapGetters } from 'vuex'

export default {
  components: {
    ControlBtn,
  },
  props: {
    estopBtn: {
      type: Boolean,
    },
    estopHighlight: {
      type: Boolean,
    },
    motorBtn: {
      type: Boolean,
    },
    motorHighlight: {
      type: Boolean,
    },
  },
  computed: {
    ...mapGetters(['isSpotFullscreen', 'isSpotStand']),
    estopPossible() {
      if (
        this.motorBtn &&
        !this.motorHighlight &&
        this.estopHighlight &&
        this.estopBtn
      )
        return true
      return false
    },
    motorPossible() {
      if (this.motorBtn) return true
      return false
    },
  },
  data() {
    return {}
  },
  methods: {
    estopClick() {
      //운행 중 비상정지로 로봇이 다치는 것을 막기 위함
      if (
        this.estop === ESTOP_STATE.NOT_ESTOPPED &&
        this.power === MOTOR_POWER.ON
      )
        return

      spotControl.estop()
    },
    motorClick() {
      if (this.power === MOTOR_POWER.ON) spotControl.powerOff()
      else if (this.power === MOTOR_POWER.OFF) spotControl.powerOn()
      //끄거나 키고 있는 중
      else this.logger('[SPOT POWER PROGRESSING] ', this.power)
    },
    setSpotFullscreen() {
      Store.commit('SET_SPOT_FULLSCREEN', !this.isSpotFullscreen)
    },
  },
}
</script>

<style lang="scss">
.main-control-group {
  position: absolute;
  top: 5.5vh;
  left: 5.5vh;
}

.estop {
  .icon {
    opacity: 1;
  }

  &.active {
    border-color: #c51212;
    background-color: #a40014;
    .icon {
      opacity: 1;
    }
  }

  &.inactive {
    border-color: #c51212;
    background-color: #a40014;
    .icon {
      opacity: 0.4;
    }
  }
}

.motor {
  &.inactive {
    background-color: #3c3c3f;
    border-color: #979797;
    .icon {
      opacity: 0.4;
    }
  }

  &.possible {
    .icon {
      opacity: 1;
    }
  }

  &.active {
    border-color: #ffe23d;
    background-color: #edc000;
    .icon {
      opacity: 1;
    }
  }

  &.active-disabled {
    border-color: #ffe23d;
    background-color: #edc000;
    .icon {
      opacity: 0.4;
    }
  }
}

.fullscreen {
  border-color: #979797;
  background-color: #3c3c3f;
}
</style>
