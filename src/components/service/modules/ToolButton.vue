<template>
  <tooltip :content="text" placement="bottom">
    <button
      slot="body"
      class="tool"
      :class="{ active: active, disabled: disabled }"
      v-on="$listeners"
    >
      <img v-if="icActive" :src="activeSrc" />
      <img v-else-if="src" :src="src" />
      <slot v-else></slot>
    </button>
  </tooltip>
</template>

<script>
import Tooltip from 'Tooltip'
export default {
  name: 'ToolButton',
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
    icActive: {
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
.tool {
  position: relative;
  display: flex;
  width: 3.571rem;
  height: 3.571rem;
  font-size: 0.857rem;
  background-color: transparent;
  border-left: solid 1px #d8d8d8;
  opacity: 0.6;

  &:first-child {
    border-left: none;
  }
  &:hover,
  &:active {
    opacity: 1;
  }
  &.active {
    opacity: 1;
    &:before {
      position: absolute;
      top: 50%;
      left: 50%;
      width: 2.714rem;
      height: 2.714rem;
      background-color: #494a4e;
      border-radius: 2px;
      transform: translate(-50%, -50%);
      content: '';
    }
  }
  &.disabled {
    cursor: default;
    opacity: 0.5;
    > img {
      cursor: default;
      opacity: 0.5;
    }
  }

  > img {
    z-index: 1;
  }
}
</style>
