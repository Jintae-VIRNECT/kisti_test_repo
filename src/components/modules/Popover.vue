<template>
  <span class="popover--wrapper" @click.stop>
    <transition name="popover">
      <div
        ref="popover"
        v-show="visible"
        role="tooltip"
        :id="`popover-${_uid}`"
        :style="style"
        class="popover"
        :class="popperClass"
        @click.stop
      >
        <div class="popover--header" v-if="title || $slots['header']">
          <p v-if="!$slots['header']" v-html="title" class="popover--title">{{ title }}</p>
          <slot name="header"></slot>
        </div>
        <div class="popover--body">
          <slot></slot>
        </div>
      </div>
    </transition>

    <!-- 버튼영역 -->
    <slot ref="reference" name="reference"></slot>
  </span>
</template>

<script>
function calcOffset(element) {
  let top = 0;
  let left = 0;

  do {
    top += element.offsetTop || 0;
    left += element.offsetLeft || 0;
    element = element.offsetParent;
  } while (element);

  return {
    top: top,
    left: left
  };
}

export default {
  name: "Popover",
  props: {
    title: String,
    trigger: {
      type: String,
      validate(value) {
        return ["click", "hover", "focus", "manual"].indexOf(value) >= 0;
      },
      default: "click"
    },
    placement: {
      type: String,
      default: "top",
      validate(value) {
        return (
          [
            "top",
            "top-start",
            "top-end",
            "right",
            "right-start",
            "right-end",
            "bottom",
            "bottom-start",
            "bottom-end",
            "left",
            "left-start",
            "left-end"
          ].indexOf(value) >= 0
        );
      }
    },
    width: {
      type: Number,
      default: 240
    },
    popperClass: String,
    show: Function,
    hide: Function
  },
  data() {
    return {
      visible: false,
      style: {
        top: 0,
        left: 0,
        width: this.width + "px"
      }
    };
  },
  watch: {
    trigger() {
      this.changeTrigger();
    }
  },
  methods: {
    changeTrigger() {
      window.removeEventListener("click", this.windowClickHandler);
      this.$el.removeEventListener("mouseenter", this.showPopover);
      this.$el.removeEventListener("mouseleave", this.hidePopover);
      this.$el.removeEventListener("click", this.togglePopover);
      this.$el.removeEventListener("focus", this.showPopover);
      this.$el.removeEventListener("blur", this.hidePopover);

      switch (this.trigger) {
        case "click":
          this.$el.addEventListener("click", this.togglePopover);
          window.addEventListener("click", this.windowClickHandler);
          break;
        case "hover":
          this.$el.addEventListener("mouseenter", this.showPopover);
          this.$el.addEventListener("mouseleave", this.hidePopover);
          break;
        case "focus":
          this.$el.addEventListener("focus", this.showPopover);
          this.$el.addEventListener("blur", this.hidePopover);
          break;
        case "manual":
          this.$el.addEventListener("click", this.togglePopover);
          break;
      }
    },
    showPopover() {
      this.$eventBus.$emit("popover:close");

      //Popover 이동
      if (this.$refs["popover"]) {
        this.$root.$el.append(this.$refs["popover"]);
        // document.body.append(this.$refs['popover'])
      }
      this.visible = true;

      this.$nextTick(() => {
        const popover = this.$refs["popover"];
        const reference = this.$slots["reference"][0].elm;
        let top = calcOffset(reference).top;
        let left = calcOffset(reference).left;

        //Popover 위치 계산 - left
        if (this.placement.indexOf("right") > -1) {
          left += reference.offsetWidth;
        } else if (this.placement.indexOf("left") > -1) {
          left -= popover.offsetWidth;
        } else {
          if (this.placement.indexOf("start") > -1) {
            //nothing
          } else if (this.placement.indexOf("end") > -1) {
            left -= popover.offsetWidth - reference.offsetWidth;
          } else {
            left -= (popover.offsetWidth - reference.offsetWidth) / 2;
          }
        }

        //Popover 위치 계산 - top
        if (this.placement.indexOf("top") > -1) {
          top -= popover.offsetHeight;
        } else if (this.placement.indexOf("bottom") > -1) {
          top += reference.offsetHeight;
        } else {
          if (this.placement.indexOf("start") > -1) {
            //nothing
          } else if (this.placement.indexOf("end") > -1) {
            top -= popover.offsetHeight - reference.offsetHeight;
          } else {
            top -= (popover.offsetHeight - reference.offsetHeight) / 2;
          }
        }
        this.$set(this.style, "top", top + "px");
        this.$set(this.style, "left", left + "px");
        this.$set(this.style, "width", this.width + "px");

        if (this.show) {
          this.show();
        }
      });
    },
    hidePopover() {
      this.visible = false;

      if (this.hide) {
        this.hide();
      }
    },
    togglePopover() {
      if (true === this.visible) {
        this.hidePopover();
      } else {
        this.showPopover();
      }
    },
    windowClickHandler() {
      this.hidePopover();
    }
  },

  /* Lifecycles */
  mounted() {
    this.changeTrigger();
    this.$eventBus.$on("popover:close", this.hidePopover);
  },
  beforeDestroy() {
    window.removeEventListener("click", this.windowClickHandler);
    this.$eventBus.$off("popover:close");
  }
};
</script>

<style lang="scss">
.popover--wrapper {
  display: inline-block;
}
.popover {
  overflow: hidden;
  position: absolute;
  top: 0;
  left: 0;
  min-width: 240px;
  border-radius: 6px;
  box-shadow: 0 2px 4px 0 rgba(16, 16, 17, 0.5);
  background-color: #fff;
  z-index: 100;

  &--header {
    position: relative;
    padding: 18px 28px;
    border-bottom: 1px solid #ddd;
  }

  &--title {
    color: #333333;
    font-size: 16px;
    font-weight: 500;
  }

  &--body {
    overflow: hidden;
    position: relative;
    max-height: 80vh;
    padding: 15px 0 15px 24px;
  }
}

.popover-enter-active {
  transition-property: transform, opacity;
  transition-duration: 0.3s;
}
.popover-enter {
  transform: translateY(5%);
  opacity: 0;
}
.popover-enter-to {
  transform: translateX(0);
  opacity: 1;
}
</style>
