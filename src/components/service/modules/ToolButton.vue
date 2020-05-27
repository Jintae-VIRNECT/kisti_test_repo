<template>
  <tooltip :content="text" placement="bottom">
    <button
      slot="body"
      class="tool"
      :class="{ active: active }"
      v-on="$listeners"
    >
      <img v-if="onActive" :src="activeSrc" />
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
    onActive: {
      type: Boolean,
      default: false,
    },
    activeSrc: {
      type: String,
      default: null,
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
.tool {
  position: relative;
  display: flex;
  width: 50px;
  height: 50px;
  font-size: 12px;
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
      width: 38px;
      height: 38px;
      background-color: #494a4e;
      border-radius: 2px;
      transform: translate(-50%, -50%);
      content: '';
    }
  }

  > img {
    z-index: 1;
  }
}
</style>
