<template>
  <button class="icon-button" @click="clickListener">
    <img :src="imgSrc" :class="animateClass" />
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
    animation: {
      type: String,
      default: null,
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
  padding: 0.571em 1.071em 0.571em 0.714em;
  color: rgba(#d2d2d2, 0.8);
  font-size: 0.929em;
  line-height: 1.429em;
  background: #38383a 0.929em 50%/1.571em no-repeat;
  border-radius: 2px;
  opacity: 0.4;

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
}
</style>
