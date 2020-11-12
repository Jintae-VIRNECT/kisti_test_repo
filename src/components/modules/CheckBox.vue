<template>
  <div class="checkbox" :class="{ toggle: value }" @click="changeToggle">
    <span class="checkbox-toggle" :class="{ toggle: value }">
      {{ type }}
    </span>
    <span class="checkbox-text" v-if="text && text.length > 0">{{ text }}</span>
  </div>
</template>

<script>
export default {
  name: 'CheckBox',
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
.checkbox {
  display: flex;
  width: 10.5714em;
  padding: 0.571rem;
  // background-color: #212125;
  // border: solid 1px #171718;
  border-radius: 3px;
  cursor: pointer;
  transition: all 0.3s;
  &.toggle {
    // background-color: $color_primary;
  }
}
.checkbox-text {
  margin: auto;
  margin-left: 0.714rem;
  // color: rgba($color_white, 0.6);
  color: #434b58;
  font-weight: 500;
  font-size: 14px;
  line-height: 1.429rem;
}
.checkbox-toggle {
  position: relative;
  width: 24.006px;
  height: 24.006px;
  // background-color: rgb(209, 77, 77);
  // background-color: #17171a;
  // border: solid 1px #333335;
  background-color: #ffffff;
  border: 1px solid #c2c6ce;
  border-radius: 2px;
  transition: all 0.3s;
  @include ir();
  &.toggle {
    background-color: $color_primary;
    border-color: transparent;
    &:after {
      position: absolute;
      top: 5.002px;
      left: 5.002px;
      width: 11.996px;
      height: 5.006px;
      // border-bottom: solid 3px $color_primary;
      // border-left: solid 3px $color_primary;
      border-bottom: solid 2px #ffffff;
      border-left: solid 2px #ffffff;
      transform: rotate(-45deg);
      content: '';
    }
  }
}
</style>
