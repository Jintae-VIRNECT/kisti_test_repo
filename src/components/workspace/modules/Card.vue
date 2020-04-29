<template>
  <div class="card" :style="{ width: cardWidth, height: cardHeight }">
    <popover
      v-if="menu"
      trigger="click"
      placement="bottom-start"
      :popperClass="popoverClass"
      :width="120"
      :scrollHide="true"
    >
      <button slot="reference" class="card__button"></button>
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
  name: 'Card',
  components: {
    Popover,
  },
  props: {
    width: {
      type: [Number, String],
      default: 204,
    },
    height: {
      type: [Number, String],
      default: 244,
    },
    menu: {
      type: Boolean,
      default: false,
    },
    popoverClass: {
      type: String,
      default: '',
    },
  },
  data() {
    return {}
  },
  computed: {
    cardWidth() {
      if (typeof this.width === 'string') {
        return this.width
      } else {
        return this.width + 'px'
      }
    },
    cardHeight() {
      if (typeof this.height === 'string') {
        return this.height
      } else {
        return this.height + 'px'
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
.card {
  position: relative;
  padding: 2.143em;
  background-color: $color_darkgray_500;
  border: solid 1px $color_darkgray_500;
  border-radius: 2px;
  > .popover--wrapper {
    position: absolute;
    top: 1.143em;
    right: 0.571em;
  }
}
.card__button {
  position: relative;
  width: 2rem;
  height: 2rem;
  background: url(~assets/image/ic-more-horiz-light.svg) 50% no-repeat;
  &:hover {
    &:before {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-color: rgba(#fff, 0.05);
      border-radius: 50%;
      content: '';
    }
  }
}
</style>
