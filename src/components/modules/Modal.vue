<template>
  <transition name="modal">
    <div v-if="visible" class="modal" :class="customClass" @wheel="scroll">
      <div class="modal--dimmed" @click.stop="doClose($event)"></div>
      <div
        class="modal--inner"
        :style="[innerWidth, innerHeight]"
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
    </div>
  </transition>
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
      type: [Number, String],
      default: 360,
    },
    height: {
      type: [Number, String],
      default: 'auto',
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
      if (typeof this.width === 'string') {
        return {
          width: this.width,
        }
      } else {
        return {
          width: this.width + 'px',
        }
      }
    },
    innerHeight() {
      if (typeof this.height === 'string') {
        return {
          height: this.height,
        }
      } else {
        return {
          height: this.height + 'px',
        }
      }
    },
  },
  watch: {
    visible(value) {
      if (value) {
        document.querySelector('body').classList.add('modal-open')
      } else {
        document.querySelector('body').classList.remove('modal-open')
      }
    },
  },
  methods: {
    scroll(e) {
      e.preventDefault()
      e.stopPropagation()
    },
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
@import '~assets/style/vars';
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
  background-color: rgba(#121517, 0.5);

  .modal--dimmed {
    position: absolute;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
  }

  .modal--inner {
    position: relative;
    min-width: 25.714em;
    background-color: #1e1e20;
    border: 1px solid rgba(#a9a9a9, 0.08);
    border-radius: 0.714em;
    box-shadow: 0 0 0.714em 0 rgba($color_darkgray_1000, 0.07),
      0 0.857em 0.857em 0 rgba($color_darkgray_1000, 0.3);
  }

  .modal--header {
    position: relative;
    height: 5em;
    padding: 1.571em 2.143em;
    background-color: $color_darkgray_500;
    border-bottom: 1px solid rgba(#7f7f7f, 0.2);
    border-radius: 0.714em 0.714em 0 0;
  }

  .modal--title {
    color: #dedede;
    // font-weight: 500;
    font-size: 1.143em;
  }

  .modal--close {
    position: absolute;
    top: 1.714em;
    right: 1.857em;
    width: 1.714em;
    height: 1.714em;
    overflow: hidden;
    text-indent: -99px;
    background: url(~assets/image/call/ic-close-w.svg) 50%/2em no-repeat;
    opacity: 0.5;

    &:hover {
      opacity: 1;
    }
  }

  .modal--body {
    position: relative;
    height: calc(100% - 5em);
    // max-height: 80vh;
    padding: 1.714em 2em;
    overflow: hidden;
  }

  .modal--footer {
    padding: 1.714em 2em 2.429em;
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
