<template>
  <tooltip :content="text" placement="bottom" customClass="tooltip-menu">
    <button
      slot="body"
      class="menu"
      :class="{ active: active, disabled: disabled }"
      v-on="$listeners"
    >
      <img v-if="isActive" :src="activeSrc" :class="{ waiting: isWaiting }" />
      <img v-else-if="src" :src="src" />
      <slot v-else></slot>
    </button>
  </tooltip>
</template>

<script>
import Tooltip from 'Tooltip'
export default {
  name: 'MenuButton',
  components: {
    Tooltip,
  },
  data() {
    return {}
  },
  props: {
    text: String,
    active: {
      type: Boolean,
      default: false,
    },
    src: String,
    isActive: {
      type: Boolean,
      default: false,
    },
    activeSrc: {
      type: String,
      default: null,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    isWaiting: {
      type: Boolean,
      default: false,
    },
  },
  watch: {},
  methods: {},

  /* Lifecycles */
  beforeDestroy() {},
  mounted() {},
}
</script>

<style lang="scss" scoped>
@import '~assets/style/vars';
.tooltip-menu {
  margin-left: 0.714rem;
}
.menu {
  position: relative;
  display: flex;
  width: 2.143rem;
  height: 2.143rem;
  font-size: 0.857rem;
  background-color: transparent;
  border-left: solid 1px $color_text_main;
  border-radius: 2px;
  opacity: 0.6;

  &:first-child {
    border-left: none;
  }
  &:hover,
  &:active {
    opacity: 1;
  }
  &.active {
    background-color: rgba($color_sub_border, 0.3);
    opacity: 1;
  }
  &.disabled {
    cursor: default;
    opacity: 0.3;
  }

  > img {
    z-index: 1;
    width: 1.429rem;
    height: 1.429rem;
    margin: auto;

    &.waiting {
      -webkit-animation-name: blinker;
      animation-name: blinker;

      -webkit-animation-duration: 1s;
      animation-duration: 1s;

      -webkit-animation-timing-function: ease-in-out;
      animation-timing-function: ease-in-out;

      -webkit-animation-iteration-count: infinite;
      animation-iteration-count: infinite;

      -webkit-animation-direction: alternate;
      animation-direction: alternate;
    }
  }
}
@keyframes blinker {
  0% {
    opacity: 0;
  }
  25% {
    opacity: 0.25;
  }
  50% {
    opacity: 0.5;
  }
  75% {
    opacity: 0.7;
  }
  100% {
    opacity: 1;
  }
}
</style>
