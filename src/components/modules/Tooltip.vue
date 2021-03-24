<template>
  <div
    class="tooltip"
    @mouseenter="enter"
    @mouseleave="leave"
    :class="customClass"
    @touchstart="enter"
  >
    <slot name="body"></slot>
    <transition name="opacity">
      <div
        class="tooltiptext"
        :class="[placement, effect]"
        v-show="show && active"
        :style="`width: ${width}`"
      >
        <p v-html="content"></p>
        <div class="arrow"></div>
      </div>
    </transition>
  </div>
</template>

<script>
export default {
  name: 'Tooltip',
  data() {
    return {
      show: false,
      timeout: null,
    }
  },
  props: {
    active: {
      // false 일때 hover 해도 툴팁 미표출
      type: Boolean,
      default: true,
    },
    guide: {
      type: Boolean,
      default: false,
    },
    content: {
      type: String,
      require: true,
    },
    width: {
      type: String,
      default: 'auto',
    },
    customClass: {
      type: String,
      default: '',
    },
    effect: {
      type: String,
      default: 'black',
    },
    placement: {
      type: String,
      default: 'bottom',
      validate(value) {
        return (
          [
            'top',
            'top-start',
            'top-end',
            'right',
            'right-start',
            'right-end',
            'bottom',
            'bottom-start',
            'bottom-end',
            'left',
            'left-start',
            'left-end',
          ].indexOf(value) >= 0
        )
      },
    },
  },
  methods: {
    enter(e) {
      if (this.isTablet) {
        if (e.type === 'mouseenter') return
        this.show = true
        clearTimeout(this.timeout)
        if (!this.guide) {
          this.timeout = setTimeout(() => {
            clearTimeout(this.timeout)
            this.show = false
          }, 2000)
        } else {
          this.timeout = setTimeout(() => {
            clearTimeout(this.timeout)
            this.show = false
          }, 5000)
        }
      } else {
        this.show = true
      }
      this.$nextTick(() => {
        const tooltip = this.$el.querySelector('.tooltiptext')
        const slot = this.$slots['body'][0].elm
        const arrow = this.$el.querySelector('.arrow')

        let tooltipWidth = tooltip.offsetWidth
        let slotWidth = slot.offsetWidth
        let tooltipHeight = tooltip.offsetHeight
        let slotHeight = slot.offsetHeight

        tooltip.style.top = 'auto'
        tooltip.style.bottom = 'auto'
        tooltip.style.left = 'auto'
        tooltip.style.right = 'auto'
        arrow.style.top = 'auto'
        arrow.style.bottom = 'auto'
        arrow.style.left = 'auto'
        arrow.style.right = 'auto'

        // bottom: 100%;
        if (this.placement.indexOf('top') > -1) {
          tooltip.style.bottom = '120%'
          arrow.style.top = '100%'
          if (this.placement.indexOf('start') > -1) {
            tooltip.style.left = '0'
            arrow.style.left = slotWidth / 2 + 'px'
          } else if (this.placement.indexOf('end') > -1) {
            tooltip.style.right = '0'
            arrow.style.right = slotWidth / 2 + 'px'
          } else {
            tooltip.style.left = (slotWidth - tooltipWidth) / 2 + 'px'
            arrow.style.left = '50%'
          }
          // top: 100%;
        } else if (this.placement.indexOf('bottom') > -1) {
          tooltip.style.top = '120%'
          arrow.style.bottom = '100%'
          if (this.placement.indexOf('start') > -1) {
            tooltip.style.left = '0'
            arrow.style.left = slotWidth / 2 + 'px'
          } else if (this.placement.indexOf('end') > -1) {
            tooltip.style.right = '0'
            arrow.style.right = slotWidth / 2 + 'px'
          } else {
            tooltip.style.left = (slotWidth - tooltipWidth) / 2 + 'px'
            arrow.style.left = '50%'
          }
        } else if (this.placement.indexOf('left') > -1) {
          tooltip.style.right = '100%'
          arrow.style.left = '100%'
          if (this.placement.indexOf('start') > -1) {
            tooltip.style.top = '0'
            arrow.style.top = slotHeight / 2 + 'px'
          } else if (this.placement.indexOf('end') > -1) {
            tooltip.style.bottom = '10px'
            arrow.style.bottom = (slotHeight - 10) / 2 + 'px'
          } else {
            tooltip.style.top = (slotHeight - tooltipHeight) / 2 + 'px'
            arrow.style.top = '50%'
          }
        } else {
          tooltip.style.left = '100%'
          arrow.style.right = '100%'
          if (this.placement.indexOf('start') > -1) {
            tooltip.style.top = '0'
            arrow.style.top = slotHeight / 2 + 'px'
          } else if (this.placement.indexOf('end') > -1) {
            tooltip.style.bottom = '10px'
            arrow.style.bottom = (slotHeight - 10) / 2 + 'px'
          } else {
            tooltip.style.top = (slotHeight - tooltipHeight) / 2 + 'px'
            arrow.style.top = '50%'
          }
        }
      })
    },
    leave() {
      this.show = false
    },
  },
  mounted() {},
}
</script>

<style lang="scss">
@import '~assets/style/vars';

.tooltip {
  position: relative;
  display: flex;
}

.tooltip .tooltiptext {
  &.black {
    --tooltip-bg-color: #4f515a;
    --tooltip-text-color: #{$color_text_sub};
  }
  &.blue {
    --tooltip-bg-color: #{$color_primary};
    --tooltip-text-color: #fff;
  }

  position: absolute;
  z-index: 99;
  // display: none;
  width: max-content;
  padding: 0.571em 0.929em;
  color: var(--tooltip-text-color);
  font-size: 0.929em;
  background-color: var(--tooltip-bg-color);
  border-radius: 4px;
  > p {
    line-height: 1.8em;
    white-space: nowrap;
  }
  .arrow {
    position: absolute;
    // display: none;
    border: solid 6px transparent;
    content: ' ';
    &:after {
      position: absolute;
      left: 0;
      width: 0;
      height: 0;
      border: solid 5px transparent;
      content: '';
    }
  }
  // border: solid 1px rgba(#d2d2d2, 0.3);
  &.top .arrow,
  &.top-start .arrow,
  &.top-end .arrow {
    &:after {
      top: 1px;
      margin-top: -7px;
      margin-left: -5px;
      border-top-color: var(--tooltip-bg-color);
    }
  }
  &.bottom .arrow,
  &.bottom-start .arrow,
  &.bottom-end .arrow {
    &:after {
      bottom: 1px;
      margin-bottom: -7px;
      margin-left: -5px;
      border-bottom-color: var(--tooltip-bg-color);
    }
  }
  &.left .arrow,
  &.left-start .arrow,
  &.left-end .arrow {
    &:after {
      left: 1px;
      margin-left: -7px;
      border-left-color: var(--tooltip-bg-color);
    }
  }
  &.right .arrow,
  &.right-start .arrow,
  &.right-end .arrow {
    &:after {
      top: -5px;
      border-right-color: var(--tooltip-bg-color);
    }
  }
}

.tooltip {
  .top {
    &,
    &-start,
    &-end {
      bottom: 120%;
      margin-bottom: 10px;
      &::before {
        margin-top: 1px;
        border-top: 1px solid vars(--tooltip-bg-color);
      }
      .arrow {
        top: 100%;
        left: 50%;
        margin-left: -5px;
      }
    }
  }
  .bottom {
    &,
    &-start,
    &-end {
      top: 120%;
      &::before {
        bottom: 100%;
        left: 50%;
        margin-bottom: 2px;
        margin-left: -5px;
        border-bottom: 2px solid vars(--tooltip-bg-color);
      }
      .arrow {
        bottom: 100%;
        left: 50%;
        margin-left: -5px;
      }
    }
  }
  .right {
    &,
    &-start,
    &-end {
      left: 105%;
      margin-left: 10px;
      &::before {
        top: 50%;
        right: 100%;
        margin-top: -5px;
        margin-top: 2px;
        border-top: 2px solid vars(--tooltip-bg-color);
      }
      .arrow {
        top: 50%;
        right: 100%;
        margin-top: -5px;
      }
    }
  }
  .left {
    &,
    &-start,
    &-end {
      right: 105%;
      margin-right: 10px;
      &::before {
        margin-top: 1px;
        border-top: 1px solid vars(--tooltip-bg-color);
      }
      .arrow {
        top: 50%;
        left: 100%;
        margin-top: -5px;
      }
    }
  }
}

.tooltip.tooltip-guide {
  > .tooltiptext.blue {
    padding: 1.143rem 1.714rem;
    box-shadow: 0 0 3px #000;
    > .arrow {
      border-width: 0.714rem;
      &:after {
        border-width: 0.643rem;
      }
    }
    &.right {
      margin-left: 0.8rem;
      > .arrow {
        margin-top: -1rem;
        &:after {
          left: -0.571rem;
        }
      }
    }
    &.bottom {
      margin-top: 0.5rem;
      > .arrow {
        margin-left: -1rem;
        &:after {
          top: -0.571rem;
        }
      }
    }
  }
}
</style>
