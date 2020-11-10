<template>
  <tool-button
    customClass="fullscreen-button"
    :text="$t('확대 / 축소')"
    :isActive="!status"
    :src="require('assets/image/call/ic_fullscreen_on.svg')"
    :activeSrc="require('assets/image/call/ic_fullscreen_off.svg')"
    placement="top"
    @click="clickHandler()"
  ></tool-button>
</template>

<script>
import ToolButton from 'ToolButton'

export default {
  name: 'ToolFullscreen',
  components: {
    ToolButton,
  },
  props: {
    status: {
      type: Boolean,
      default: true,
    },
  },
  methods: {
    clickHandler() {
      this.$eventBus.$emit('fullscreen', this.status)
      this.$emit('update:status', !this.status)
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>

<style lang="scss">
.tooltip.fullscreen-button {
  position: absolute;
  right: 1.143rem;
  bottom: 1.143rem;
  width: 2.714rem;
  height: 2.714rem;
  background: rgba(#000, 0.4);
  border-radius: 2px;
  transition: all 0.3s;
  > .tool {
    width: 100%;
    height: 100%;
    > img {
      width: 1.714rem;
      height: 1.714rem;
      margin: auto;
    }
  }
  &:hover {
    background: #636977;
  }
}
</style>
