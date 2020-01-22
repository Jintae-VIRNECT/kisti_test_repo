<template>
<div v-if="visible"
  class="modal"
  :class="customClass">
  <div class="modal--dimmed" @click.stop="doClose($event)"></div>
  <transition name="modal">
    <div v-show="_isMounted"
      class="modal--inner"
      :style="innerWidth"
      @click="clickHander">
      <div class="modal--header">
        <p class="modal--title" v-html="title">Modal Header</p>
        <button v-if="true === showClose" class="modal--close" @click="doClose($event)">close</button>
        <slot name="header"></slot>
      </div>
      
      <div class="modal--body">
        <slot></slot>
      </div>

      <div class="modal--footer" v-if="$slots['footer']">
        <slot name="footer"></slot>
      </div>

      <div class="hidden" v-if="$slots['hidden-header']" :class="{'hidden__active' : hidden}">
        <div class="hidden--inner">
          <div class="modal--header">
            <button v-if="true === showClose" class="modal--close" @click="doClose($event)">close</button>
            <slot name="hidden-header"></slot>
          </div>
          
          <div class="modal--body">
            <slot name="hidden-body"></slot>
          </div>

          <div class="modal--footer" v-if="$slots['footer']">
            <slot name="hidden-footer"></slot>
          </div>
          <slot name="hidden"></slot>
        </div>
      </div>
    </div>
  </transition>
</div>
</template>

<script>
export default {
  name: "Modal",
  props: {
    visible: {
      type: Boolean,
      require: true
    },
    title: String,
    showClose: {
      type: Boolean,
      default: true
    },
    beforeClose: Function,
    width: {
      type: Number,
      default: 360
    },
    eventPropagation: {
      type: Boolean,
      default: true
    },
    customClass: {
      type: String
    },
    hidden: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {}
  },
  computed: {
    innerWidth() {
      return {
          width: this.width+'px'
      }
    }
  },
  methods: {
    clickHander(event) {
      if(this.eventPropagation) {
        event.stopPropagation()
      }

      if( this.$listeners['click'] ) {
        this.$listeners['click'](event)
      }
    },
    doClose() {
      if(this.beforeClose) {
        this.beforeClose()
      }
      
      this.$emit('update:visible',false)
    }
  },

  /* Lifecycles */
  mounted() {
  }
}
</script>

<style lang="scss">
.modal {
  position: fixed;
  top: 0;
  left: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  z-index: 100;
  background-color: rgba(18, 21, 23, .5);

  &--dimmed {
    position: absolute;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;    
  }

  &--inner {
    position: relative;
    min-width: 360px;
    border-radius: 10px;
    background-color: #fff;
  }

  &--header {
    position: relative;
    padding: 24px 28px;
    border-bottom: 1px solid #ddd;
  }

  &--title {
    color: #333333;
    font-size: 16px;
    font-weight: 500;
  }

  &--close {
    overflow: hidden;
    position: absolute;
    top: 24px;
    right: 26px;
    width: 24px;
    height: 24px;
    opacity: .5;
    background: url(~assets/image/call/ic-close-w.svg) 50%/28px no-repeat;
    text-indent: -99px;

    &:hover { opacity: 1; }
  }

  &--body {
    overflow: hidden;
    position: relative;
    max-height: 80vh;
    padding: 24px 28px;
  }

  &--footer {
    border-top: 1px solid #dddddd;
    padding: 24px 28px 34px;
  }
}

.modal-enter-active {
  transition-property: transform ,opacity;
  transition-duration: .3s;
}
.modal-enter {
  transform: translateY(5%);
  opacity: 0;
}
  .modal-enter-to {
  transform: translateX(0);
  opacity: 1;
}
</style>
