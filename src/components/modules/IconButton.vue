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
  padding: 0.7143rem 1.2857rem 0.7143rem 1.2857rem;
  color: #0b1f48;
  font-weight: normal;
  font-size: 0.9286rem;
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
    margin-right: 0.2857rem;
  }

  &.active {
    color: #ffffff;
    background: #1665d8 0.929em 50%/1.571em no-repeat;
  }
}
</style>
