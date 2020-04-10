<template>
  <div class="card" :style="{ width: cardWidth, height: height + 'px' }">
    <popover v-if="menu" trigger="click" placement="bottom-start">
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
    image: {
      type: String,
      default: '',
    },
    width: {
      type: [Number, String],
      default: 204,
    },
    height: {
      type: Number,
      default: 244,
    },
    menu: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {}
  },
  computed: {
    cardWidth() {
      if (this.width === 'full') {
        return '100%'
      } else {
        return this.width + 'px'
      }
    },
  },
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>

<style lang="scss" scoped>
.card {
  position: relative;
  padding: 30px;
  background-color: #313135;
  border: solid 1px #3e3e42;
  border-radius: 2px;
  > .popover--wrapper {
    position: absolute;
    top: 16px;
    right: 16px;
  }
}
.card__button {
  width: 28px;
  height: 28px;
  background: url(~assets/image/ic-more-horiz-light.svg) 50% no-repeat;
}
</style>
