<template>
  <section
    class="main-control-group"
    :class="{
      center: isSpotFullscreen,
    }"
  >
    <control-btn
      class="estop"
      :disabled="motorBtn && motorHighlight && estopHighlight && !estopBtn"
      :class="{
        active: motorBtn && !motorHighlight && estopHighlight && estopBtn,
        inactive: motorBtn && motorHighlight && estopHighlight && !estopBtn,
      }"
      :imgSrc="require('assets/image/spot/ic_estop.svg')"
      @click="estopClick"
    ></control-btn>
    <control-btn
      :disabled="!motorPossible"
      class="motor"
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
    estop: {
      type: String,
    },
    power: {
      type: String,
    },
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

      this.logger('[SPOT] estop')
      spotControl.estop()
    },
    motorClick() {
      if (this.power === MOTOR_POWER.ON) {
        this.logger('[SPOT] motor power off')
        spotControl.powerOff()
      } else if (this.power === MOTOR_POWER.OFF) {
        this.logger('[SPOT] motor power on')
        spotControl.powerOn()
      }
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
@import '~assets/style/mixin';

.main-control-group {
  position: absolute;
  top: 5.5vh;
  left: 5.5vh;
  z-index: 3;
  transition: 0.7s;

  &.center {
    left: 50%;
    transform: translateX(-50%);
  }
}

.estop {
  .icon {
    opacity: 1;
  }

  &.active {
    background-color: #a40014;
    border-color: #c51212;
    .icon {
      opacity: 1;
    }
  }

  &.inactive {
    background-color: #a40014;
    border-color: #c51212;
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
    background-color: #edc000;
    border-color: #ffe23d;
    .icon {
      opacity: 1;
    }
  }

  &.active-disabled {
    background-color: #edc000;
    border-color: #ffe23d;
    .icon {
      opacity: 0.4;
    }
  }
}

.fullscreen {
  background-color: #3c3c3f;
  border-color: #979797;
}

@include responsive-tablet-mobile {
  .main-control-group .spot-control-btn {
    border-radius: 3px;
  }
}

@include responsive-tablet {
  .main-control-group {
    top: 2.9vw;
    left: 50%;
    transform: translateX(-50%);

    .fullscreen {
      display: none;
    }

    .spot-control-btn {
      width: 56px;
      height: 56px;
    }
  }
}

@include responsive-mobile {
  .main-control-group {
    top: 20px;
    left: 50%;
    transform: translateX(-50%);

    .fullscreen {
      display: none;
    }

    .spot-control-btn {
      width: 4.8rem;
      height: 4.8rem;
    }
  }
}
</style>
