<template>
  <button
    class="icon-button"
    @click="clickListener"
    @mousedown="toggleMouse(true, true)"
    @mouseup="toggleMouse(false, true)"
    @mouseover="toggleMouse(false, true)"
    @mouseout="toggleMouse(false, false)"
    :class="{ select: select, colored: colored }"
  >
    <img
      v-if="showSelectImg"
      :src="selectImgSrc"
      :class="animateClass"
      alt=""
    />
    <img
      v-else-if="showActiveImg"
      :src="activeImgSrc"
      :class="animateClass"
      alt=""
    />
    <img v-else :src="imgSrc" :class="animateClass" alt="" />

    {{ text }}
  </button>
</template>

<script>
export default {
  name: 'IconButton',
  props: {
    text: {
      type: String,
      default: '',
    },
    imgSrc: {
      type: String,
      default: '',
    },
    activeImgSrc: {
      type: String,
      default: '',
    },
    selectImgSrc: {
      type: String,
      default: '',
    },
    select: {
      type: Boolean,
      default: false,
    },

    colored: {
      type: Boolean,
      default: false,
    },
    animation: {
      type: String,
      default: null,
    },
  },
  data() {
    return {
      animateClass: '',
      active: false,
      hover: false,
    }
  },
  computed: {
    showActiveImg() {
      if (this.active || this.hover) {
        return true
      } else {
        return false
      }
    },
    showSelectImg() {
      if ((this.select && !this.active) || (this.select && this.hover)) {
        return true
      } else {
        return false
      }
    },
  },
  methods: {
    clickListener() {
      if (typeof this.$listeners['click'] === 'function') {
        this.$listeners['click']()
        this.animateClass = this.animation
        setTimeout(() => {
          this.animateClass = ''
        }, 400)
      }
    },
    toggleMouse(active, hover) {
      this.active = active
      this.hover = hover
    },
  },
}
</script>

<style lang="scss" scoped>
@import '~assets/style/vars';
.icon-button {
  display: flex;
  margin-left: 0.643em;
  padding: 10.0002px 11px 10.0002px 11px;
  color: #757f91;
  font-weight: normal;
  font-size: 0.9286rem;
  line-height: 1.429em;
  background: $color_white 0.929em 50%/1.571em no-repeat;
  border: 1px solid #d6d6d8;
  border-radius: 2px;
  transition: 0.3s;

  &:hover {
    color: $color_text_main;
  }
  &.active {
    color: $color_text_main;
  }
  &.select {
    color: #757f91;
  }

  &.colored {
    &.select {
      color: $color_white;
      background-color: $color_primary;
      border: 1px solid $color_primary;
    }
    &:active {
      color: $color_white;
      background-color: #0960cd;
      border: 1px solid #0960cd;
    }
    &:hover {
      color: $color_white;
      background-color: #0960cd;
      border: 1px solid #0960cd;
    }
  }

  > img {
    height: 1.429em;
    margin-right: 0.2857rem;
  }
}
</style>
