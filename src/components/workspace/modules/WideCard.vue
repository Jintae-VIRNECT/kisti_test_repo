<template>
  <div class="widecard" :style="{ height: height + 'px' }" :class="customClass">
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
      type: Number,
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
  computed: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>

<style lang="scss" scoped>
.widecard {
  position: relative;
  width: 100%;
  margin-bottom: 8px;
  padding: 22px 30px;
  background-color: #313135;
  // border: solid 1px #3e3e42;
  border-radius: 2px;

  > .popover--wrapper {
    position: absolute;
    top: 16px;
    right: 16px;
  }
}
.widecard__button {
  width: 28px;
  height: 28px;
  background: url(~assets/image/ic-more-horiz-light.svg) 50% no-repeat;
}
</style>
