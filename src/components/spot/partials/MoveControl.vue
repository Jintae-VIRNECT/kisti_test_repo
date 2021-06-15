<template>
  <section class="move-control-group">
    <control-btn
      v-for="(btn, idx) in btnList"
      :key="'cb' + idx"
      :ref="btn.ref"
      :imgSrc="btn.imgSrc"
      :disabled="!moveBtn"
      :class="{
        inactive: !moveBtn,
        pressed: key[btn.key].state,
      }"
      @mousedown="moveBtnDown(btn.key)"
      @mouseup="moveBtnUp(btn.key)"
    ></control-btn>
  </section>
</template>

<script>
import ControlBtn from '../modules/ControlBtn'
import { SPOT_CONTROL_KEYCODE } from 'configs/spot.config'
import { spotControl } from 'plugins/remote/spot/spotSocket'

export default {
  components: {
    ControlBtn,
  },
  props: {
    moveBtn: {
      type: Boolean,
    },
  },
  data() {
    return {
      btnList: [
        {
          ref: 'left-btn',
          key: SPOT_CONTROL_KEYCODE.q,
          imgSrc: require('assets/image/spot/ic_move_left.svg'),
        },
        {
          ref: 'go-btn',
          key: SPOT_CONTROL_KEYCODE.w,
          imgSrc: require('assets/image/spot/ic_move_go.svg'),
        },
        {
          ref: 'right-btn',
          key: SPOT_CONTROL_KEYCODE.e,
          imgSrc: require('assets/image/spot/ic_move_right.svg'),
        },
        {
          ref: 'left-turn-btn',
          key: SPOT_CONTROL_KEYCODE.a,
          imgSrc: require('assets/image/spot/ic_move_round_left.svg'),
        },
        {
          ref: 'back-btn',
          key: SPOT_CONTROL_KEYCODE.s,
          imgSrc: require('assets/image/spot/ic_move_back.svg'),
        },
        {
          ref: 'right-turn-btn',
          key: SPOT_CONTROL_KEYCODE.d,
          imgSrc: require('assets/image/spot/ic_move_round_right.svg'),
        },
      ],
      key: {
        87: {
          state: false, //w
          target: 'v_x',
          value: 0.5,
        },
        65: {
          state: false, //a
          target: 'v_rot',
          value: 0.5,
        },
        83: {
          state: false, //s
          target: 'v_x',
          value: -0.5,
        },
        68: {
          state: false,
          target: 'v_rot',
          value: -0.5,
        }, //d
        81: {
          state: false,
          target: 'v_y',
          value: 0.5,
        }, //q
        69: {
          state: false,
          target: 'v_y',
          value: -0.5,
        }, //e
      },
    }
  },
  methods: {
    moveBtnDown(key) {
      this.key[key].state = true
    },
    moveBtnUp(key) {
      this.key[key].state = false
    },
    onKeyDown(event) {
      if (this.moveBtn) {
        var keyCode = event.keyCode
        this.key[keyCode].state = true
      }
    },
    onKeyUp(event) {
      if (this.moveBtn) {
        var keyCode = event.keyCode
        this.key[keyCode].state = false
      }
    },
  },
  created() {
    setInterval(() => {
      if (this.moveBtn) {
        const val = {
          v_x: 0.0,
          v_y: 0.0,
          v_rot: 0.0,
        }

        Object.keys(this.key).forEach(key => {
          if (this.key[key].state) {
            const target = this.key[key].target
            val[target] = this.key[key].value
          }
        })

        if (val.v_x != 0.0 || val.v_y != 0.0 || val.v_rot != 0.0) {
          this.logger([...Object.values(val)])
          spotControl.drive([...Object.values(val)])
        }
      }
    }, 300)
    window.addEventListener('keydown', this.onKeyDown, false)
    window.addEventListener('keyup', this.onKeyUp, false)
  },
}
</script>

<style lang="scss">
.move-control-group {
  position: absolute;
  bottom: 5.5vh;
  right: 5.5vh;

  width: 22vh;

  display: flex;
  justify-content: flex-end;
  flex-wrap: wrap;

  .spot-control-btn {
    width: 5.925vh;
    height: 5.925vh;
    margin-top: 0.74vh;
    margin-left: 0.74vh;

    .icon {
      width: 1.666vh;
      height: 1.666vh;
    }

    &.inactive {
      opacity: 0.4;
    }

    &.pressed > .back {
      background-color: #74747a; //opacity로 필요
    }
  }
}
</style>
