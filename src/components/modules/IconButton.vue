<template>
  <button
    class="icon-button"
    @click="clickListener"
    :class="{ active: active }"
  >
    <img :src="active ? activeImgSrc : imgSrc" :class="animateClass" />
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
      tpye: String,
      default: '',
    },
    activeImgSrc: {
      type: String,
      default: '',
    },
    animation: {
      type: String,
      default: null,
    },
    active: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      animateClass: '',
    }
  },
  methods: {
    clickListener() {
      this.$listeners['click']()
      this.animateClass = this.animation
      setTimeout(() => {
        this.animateClass = ''
      }, 400)
    },
  },
}
</script>

<style lang="scss" scoped>
.icon-button {
  display: flex;
  margin-left: 0.643em;
  // padding: 0.571em 1.071em 0.571em 0.714em;
  padding: 10px 18px 10px 18px;
  color: #0b1f48;
  font-weight: normal;
  font-size: 13px;
  line-height: 1.429em;
  background: #edf0f4 0.929em 50%/1.571em no-repeat;
  border-radius: 2px;

  &:hover {
    opacity: 0.8;
  }
  &:active {
    opacity: 1;
  }
  > img {
    height: 1.429em;
    margin-right: 4px;
  }

  &.active {
    color: #ffffff;
    background: #1665d8 0.929em 50%/1.571em no-repeat;
  }
}
</style>
