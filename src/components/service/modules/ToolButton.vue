<template>
  <button class="tool" :class="{ active: active }" @click="$emit('action')">
    <img v-if="onActive" :src="activeSrc" />
    <img v-else :src="src" />
    <span>{{ text }}</span>
  </button>
</template>

<script>
export default {
  name: 'ToolButton',
  components: {},
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
  display: flex;
  padding: 0 16px;
  font-size: 12px;
  background-color: transparent;
  border-left: solid 1px #d8d8d8;
  opacity: 0.7;

  &:first-child {
    border-left: none;
  }
  &:hover,
  &:active {
    opacity: 1;
  }
  &.active {
    opacity: 1;
  }

  > img {
    width: 20px;
    height: 20px;
    margin-right: 7px;
  }
}
</style>
