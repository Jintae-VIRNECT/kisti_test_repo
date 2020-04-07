<template>
  <div class="switcher" :class="{ toggle: value }" @click="changeToggle">
    <!-- <div class="switcher-toggle"></div> -->
  </div>
</template>

<script>
export default {
  name: 'Switcher',
  computed: {},
  data() {
    return {}
  },
  props: {
    value: {
      type: Boolean,
      default: false,
    },
  },
  methods: {
    changeToggle() {
      console.log(!this.value)
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
  position: relative;
  width: 38px;
  height: 20px;
  // background-color: #131313;
  background-color: #0f75f5;
  border-radius: 10px;
  cursor: pointer;
  &:before {
    position: absolute;
    left: 19px;
    width: 18px;
    height: 18px;
    margin: 1px 0;
    background-color: #fff;
    border-radius: 50%;
    transition: left 0.3s;
    content: '';
  }
  &.toggle {
    background-color: #0f75f5;
    &:before {
      left: 2px;
    }
  }
}
</style>
