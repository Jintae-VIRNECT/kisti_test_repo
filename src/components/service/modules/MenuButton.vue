<template>
  <tooltip :content="text" placement="bottom">
    <button
      slot="body"
      class="menu"
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
.menu {
  position: relative;
  display: flex;
  width: 30px;
  height: 30px;
  margin-left: 10px;
  font-size: 12px;
  background-color: transparent;
  border-left: solid 1px #d8d8d8;
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
    background-color: rgba(#888888, 0.3);
    opacity: 1;
  }

  > img {
    z-index: 1;
    width: 20px;
    height: 20px;
    margin: auto;
  }
}
</style>
