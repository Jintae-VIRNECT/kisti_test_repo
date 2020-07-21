<template>
  <div class="switcher">
    <span class="switcher-text" v-if="text && text.length > 0">{{ text }}</span>
    <div
      class="switcher-toggle"
      :class="{ toggle: value }"
      @click="changeToggle"
    >
      <!-- <div class="switcher-toggle"></div> -->
      <span class="switcher-toggle__type" :class="{ toggle: value }">{{
        type
      }}</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Switcher',
  data() {
    return {}
  },
  props: {
    value: {
      type: Boolean,
      default: false,
    },
    text: {
      type: String,
      default: null,
    },
  },
  computed: {
    type() {
      if (this.value) {
        return 'ON'
      } else {
        return 'OFF'
      }
    },
  },
  methods: {
    changeToggle() {
      this.$emit('update:value', !this.value)
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>

<style lang="scss">
@import '~assets/style/mixin';
.switcher {
  display: flex;
}
.switcher-text {
  margin: auto;
  color: rgba($color_white, 0.6);
  font-weight: 500;
  font-size: 0.929rem;
}
.switcher-toggle {
  position: relative;
  width: 2.286rem;
  height: 1rem;
  margin: auto 0 auto 0.929rem;
  // background-color: rgb(209, 77, 77);
  background-color: #131313;
  border-radius: 0.571rem;
  cursor: pointer;
  transition: background-color 0.3s;
  &.toggle {
    background-color: $color_primary;
  }
}
.switcher-toggle__type {
  position: absolute;
  left: -0.214rem;
  width: 1.286rem;
  height: 1.286rem;
  margin: 1px 0;
  background-color: #fff;
  border-radius: 50%;
  transform: translateY(-0.214rem);
  transition: left 0.3s;
  @include ir();

  &.toggle {
    left: 1.214rem;
  }
}
</style>
