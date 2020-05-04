<template>
  <div class="widecard" :style="{ height: viewHeight }" :class="customClass">
    <popover v-if="menu" trigger="click" placement="bottom-start">
      <button slot="reference" class="widecard__button"></button>
      <slot name="menuPopover"></slot>
      <!-- <div>
        버튼
      </div> -->
    </popover>
    <slot></slot>
  </div>
</template>

<script>
import Popover from 'Popover'
export default {
  name: 'WideCard',
  components: {
    Popover,
  },
  props: {
    height: {
      type: [Number, String],
      default: 86,
    },
    menu: {
      type: Boolean,
      default: false,
    },
    customClass: {
      type: [Array, Object],
      default: () => {
        return {}
      },
    },
  },
  data() {
    return {}
  },
  computed: {
    viewHeight() {
      if (typeof this.height === 'string') {
        return this.height
      } else {
        return `${this.height}px`
      }
    },
  },
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>

<style lang="scss" scoped>
@import '~assets/style/vars';

.widecard {
  position: relative;
  width: 100%;
  margin-bottom: 0.571em;
  padding: 1.571em 2.143em;
  background-color: $color_darkgray_500;
  // border: solid 1px #3e3e42;
  border-radius: 2px;
  transition: background-color 0.3s;

  > .popover--wrapper {
    position: absolute;
    top: 1.143em;
    right: 1.143em;
  }
}
.widecard__button {
  width: 2em;
  height: 2em;
  background: url(~assets/image/ic_more.svg) 50% no-repeat;
}
</style>
