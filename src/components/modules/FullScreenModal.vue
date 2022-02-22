<template>
  <transition name="modal">
    <div class="fullscreen-modal" v-if="visible">
      <header class="fullscreen-modal__header" :class="{ border }">
        <h1>{{ title }}</h1>
        <button
          v-if="showClose"
          class="fullscreen-modal__close"
          @click="close"
        ></button>
      </header>
      <div class="fullscreen-modal--body">
        <slot></slot>
      </div>
    </div>
  </transition>
</template>

<script>
export default {
  name: 'FullScreenModal',
  props: {
    title: String,
    showClose: {
      type: Boolean,
      default: true,
    },
    visible: {
      type: Boolean,
      default: false,
    },
    border: {
      type: Boolean,
      default: true,
    },
  },
  methods: {
    close() {
      this.$emit('close')
    },
  },
}
</script>

<style lang="scss" scoped>
@import '~assets/style/mixin';

.fullscreen-modal {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 101;
  display: flex;
  flex-direction: column;
  background-color: $new_color_bg_sub;
  cursor: auto;
}

.fullscreen-modal__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 5.4rem;
  background-color: $new_color_bg;

  &.border {
    border-bottom: 1px solid $new_color_sub_border;
  }
  h1 {
    margin-left: 1.6rem;
    @include fontLevel(200);
    color: $new_color_text_main;
  }
  .fullscreen-modal__close {
    width: 2.4rem;
    height: 2.4rem;
    margin-right: 1.6rem;
    background: url(~assets/image/ic_close_24.svg) 50% no-repeat;
  }
}
.fullscreen-modal--body {
  display: flex;
  flex-direction: column;
  height: calc(100% - 5.4rem);
}
</style>
