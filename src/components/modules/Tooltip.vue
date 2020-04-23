<template>
  <div class="tooltip" @mouseenter="enter" @mouseleave="leave">
    <slot name="body"></slot>
    <transition name="fade-in-linear">
      <div class="tooltiptext" :class="placement + ' ' + effect" v-show="show">
        {{ content }}
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
    }
  },
  props: {
    content: {
      type: String,
      require: true,
    },
    // tooltipClass: {
    //     type: String,
    //     default: 'item'
    // },
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
    enter() {
      this.show = true
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
.tooltip {
  position: relative;
  display: inline-block;
}

.tooltip .tooltiptext {
  position: absolute;
  z-index: 3;
  // display: none;
  width: max-content;
  padding: 10px 20px;
  font-size: 13px;
  border-radius: 6px;
  .arrow {
    position: absolute;
    border-color: transparent;
    border-style: solid;
    border-width: 6px;
    content: ' ';
    &:after {
      position: absolute;
      left: 0;
      width: 0;
      height: 0;
      margin-left: -5px;
      border-color: transparent;
      border-style: solid;
      border-width: 5px;
      content: '';
    }
  }
  &.black {
    color: #fff;
    background-color: #333;
    border: solid 1px rgba(#d2d2d2, 0.3);
    &.top .arrow,
    &.top-start .arrow,
    &.top-end .arrow {
      border-top-color: rgba(#d2d2d2, 0.3);
      &:after {
        top: 1px;
        margin-top: -7px;
        border-top-color: #333;
      }
    }
    &.bottom .arrow,
    &.bottom-start .arrow,
    &.bottom-end .arrow {
      border-bottom-color: rgba(#d2d2d2, 0.3);
      &:after {
        bottom: 1px;
        margin-bottom: -7px;
        border-bottom-color: #333;
      }
    }
    &.left .arrow,
    &.left-start .arrow,
    &.left-end .arrow {
      border-left-color: rgba(#d2d2d2, 0.3);
      &:after {
        left: 1px;
        margin-left: -7px;
        border-left-color: #333;
      }
    }
    &.right .arrow,
    &.right-start .arrow,
    &.right-end .arrow {
      border-right-color: rgba(#d2d2d2, 0.3);
      &:after {
        right: 1px;
        margin-right: -7px;
        border-right-color: #333;
      }
    }
  }
  &.white {
    color: #333;
    background-color: #fff;
    border: 1px solid #333;
    &.top .arrow,
    &.top-start .arrow,
    &.top-end .arrow {
      border-color: #fff transparent transparent transparent;
    }
    &.bottom .arrow,
    &.bottom-start .arrow,
    &.bottom-end .arrow {
      border-color: transparent transparent #fff transparent;
    }
    &.left .arrow,
    &.left-start .arrow,
    &.left-end .arrow {
      border-color: transparent transparent transparent #fff;
    }
    &.right .arrow,
    &.right-start .arrow,
    &.right-end .arrow {
      border-color: transparent #fff transparent transparent;
    }
  }
}

.tooltip:hover .tooltiptext {
  // display: block;
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
        border-top: 1px solid #333;
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
        border-bottom: 2px solid #333;
      }
      .arrow {
        bottom: 100%;
        left: 50%;
        margin-left: -5px;
        // border-color: transparent transparent $arrowColor transparent;
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
        border-top: 2px solid #333;
      }
      .arrow {
        top: 50%;
        right: 100%;
        margin-top: -5px;
        // border-color: transparent black transparent transparent;
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
        border-top: 1px solid #333;
      }
      .arrow {
        top: 50%;
        left: 100%;
        margin-top: -5px;
        // border-color: transparent transparent transparent black;
      }
    }
  }
}
</style>
