<template>
  <button class="icon-button" @click="clickListener" :class="customClass">
    <img :src="imgSrc" :class="animateClass" />
    <span>{{ text }}</span>
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
    animation: {
      type: String,
      default: null,
    },
    highlight: {
      type: Boolean,
      default: false,
    },
    customClass: {
      type: [String, Object],
      default: '',
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
@import '~assets/style/mixin';

.icon-button {
  display: flex;
  margin-left: 0.643em;
  padding: 0.571em 1.071em 0.571em 0.714em;
  color: rgba(#d2d2d2, 0.8);
  font-size: 0.929em;
  line-height: 1.429em;
  white-space: nowrap;
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

  &.custom-local-record {
    opacity: 0.5;

    &:hover {
      background-color: #575759;
    }

    &.highlight {
      color: rgba(#d2d2d2, 1);
      background-color: #38383a;
      opacity: 1;
    }
  }
}

@include responsive-mobile {
  .icon-button {
    align-items: center;
    justify-content: center;
    width: 3.2rem;
    height: 3.2rem;
    margin-right: 0.8rem;
    margin-left: 0;
    padding: 0;
    background-color: $new_color_bg_icon; //@color 적용 필요
    border-radius: 0.6rem;
    opacity: 1;
    > img {
      width: 1.6rem;
      max-width: 1.6rem;
      height: 1.6rem;
      margin-right: 0;
    }
    > span {
      display: none;
    }
  }
}
</style>
