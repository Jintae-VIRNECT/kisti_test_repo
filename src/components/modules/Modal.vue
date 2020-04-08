<template>
  <div v-if="visible" class="modal" :class="customClass">
    <div class="modal--dimmed" @click.stop="doClose($event)"></div>
    <transition name="modal">
      <div
        v-show="_isMounted"
        class="modal--inner"
        :style="innerWidth"
        @click="clickHander"
      >
        <div class="modal--header">
          <p class="modal--title" v-html="title">Modal Header</p>
          <button
            v-if="true === showClose"
            class="modal--close"
            @click="doClose($event)"
          >
            close
          </button>
          <slot name="header"></slot>
        </div>

        <div class="modal--body">
          <slot></slot>
        </div>

        <div class="modal--footer" v-if="$slots['footer']">
          <slot name="footer"></slot>
        </div>

        <div
          class="hidden"
          v-if="$slots['hidden-header']"
          :class="{ hidden__active: hidden }"
        >
          <div class="hidden--inner">
            <div class="modal--header">
              <button
                v-if="true === showClose"
                class="modal--close"
                @click="doClose($event)"
              >
                close
              </button>
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
  name: 'Modal',
  props: {
    visible: {
      type: Boolean,
      require: true,
    },
    title: String,
    showClose: {
      type: Boolean,
      default: true,
    },
    beforeClose: Function,
    width: {
      type: Number,
      default: 360,
    },
    eventPropagation: {
      type: Boolean,
      default: true,
    },
    customClass: {
      type: String,
    },
    hidden: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {}
  },
  computed: {
    innerWidth() {
      return {
        width: this.width + 'px',
      }
    },
  },
  methods: {
    clickHander(event) {
      if (this.eventPropagation) {
        event.stopPropagation()
      }

      if (this.$listeners['click']) {
        this.$listeners['click'](event)
      }
    },
    doClose() {
      if (this.beforeClose) {
        this.beforeClose()
      }

      this.$emit('update:visible', false)
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>

<style lang="scss">
.modal {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background-color: rgba(18, 21, 23, 0.5);

  .modal--dimmed {
    position: absolute;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
  }

  .modal--inner {
    position: relative;
    min-width: 360px;
    background-color: #1e1e20;
    border: 1px solid rgba(#a9a9a9, 0.27);
    border-radius: 10px;
    box-shadow: 0 0 10px 0 rgba(#000, 0.07), 0 12px 12px 0 rgba(#000, 0.3);
  }

  .modal--header {
    position: relative;
    padding: 22px 30px;
    background-color: #313135;
    border-bottom: 1px solid rgba(#7f7f7f, 0.27);
    border-radius: 10px 10px 0 0;
  }

  .modal--title {
    color: #dedede;
    // font-weight: 500;
    font-size: 16px;
  }

  .modal--close {
    position: absolute;
    top: 24px;
    right: 26px;
    width: 24px;
    height: 24px;
    overflow: hidden;
    text-indent: -99px;
    background: url(~assets/image/call/ic-close-w.svg) 50%/28px no-repeat;
    opacity: 0.5;

    &:hover {
      opacity: 1;
    }
  }

  .modal--body {
    position: relative;
    max-height: 80vh;
    padding: 24px 28px;
    overflow: hidden;
  }

  .modal--footer {
    padding: 24px 28px 34px;
    // border-top: 1px solid #7f7f7f;
  }
}

.modal-enter-active {
  transition-duration: 0.3s;
  transition-property: transform, opacity;
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
