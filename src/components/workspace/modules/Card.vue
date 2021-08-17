<template>
  <div class="card" :style="{ width: cardWidth, height: cardHeight }">
    <popover
      v-if="menu"
      trigger="click"
      :placement="placement"
      :popperClass="popoverClass"
      width="auto"
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

const defaultPlacement = 'bottom-start'
const mobilePlacement = 'left-start'

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
    return {
      placement: defaultPlacement,
      responsivePlacement: null,
    }
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
  methods: {
    setDefaultPlacement() {
      this.placement = defaultPlacement
    },
    setMobilePlacement() {
      this.placement = mobilePlacement
    },
  },

  /* Lifecycles */
  mounted() {
    this.responsivePlacement = this.callAndGetMobileResponsiveFunction(
      this.setMobilePlacement,
      this.setDefaultPlacement,
    )
    this.addEventListenerScreenResize(this.responsivePlacement)
  },
  beforeDestroy() {
    this.removeEventListenerScreenResize(this.responsivePlacement)
  },
}
</script>

<style lang="scss" scoped>
@import '~assets/style/vars';
@import '~assets/style/mixin';

.card {
  position: relative;
  padding: 2.143em;
  background-color: $color_darkgray_500;
  border: solid 1px $color_darkgray_500;
  border-radius: 2px;
  transition: background-color 0.3s;
  > .popover--wrapper {
    position: absolute;
    top: 1.143em;
    right: 0.571em;
  }
}
.card__button {
  position: relative;
  display: block;
  width: 2rem;
  height: 2rem;
  background: url(~assets/image/ic_more.svg) 50%/2rem 2rem no-repeat;
  &:before {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: transparent;
    border-radius: 50%;
    transition: background-color 0.3s;
    content: '';
  }
  &:hover {
    &:before {
      background-color: rgba(#fff, 0.05);
    }
  }
}
@include responsive-mobile {
  .card > .popover--wrapper {
    top: 50%;
    right: 0.2rem;
    z-index: 99;
    transform: translateY(-50%);
  }
  .card__button {
    width: 3.2rem;
    height: 3.2rem;
    background: none;
    &:before {
      background: url(~assets/image/ic_more_vertical_24.svg) 50%/3.2rem 3.2rem
        no-repeat;
    }
    &:hover {
      &:before {
        background-color: $new_color_bg_hover;
      }
    }
  }
}
</style>
