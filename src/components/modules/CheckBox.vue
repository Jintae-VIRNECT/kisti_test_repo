<template>
  <div class="checkbox" :class="{ toggle: value }" @click="changeToggle">
    <span class="checkbox-toggle" :class="{ toggle: value }"> </span>
    <span class="checkbox-text" v-if="text && text.length > 0">{{ text }}</span>
  </div>
</template>

<script>
export default {
  name: 'CheckBox',
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
  data() {
    return {}
  },
  methods: {
    changeToggle() {
      this.$emit('update:value', !this.value)
    },
  },
}
</script>

<style lang="scss">
@import '~assets/style/mixin';
.checkbox {
  display: flex;
  max-width: 15.5714em;
  padding: 0.571rem;
  border-radius: 3px;
  cursor: pointer;
  transition: all 0.3s;
}
.checkbox-text {
  margin: auto;
  margin-left: 0.714rem;
  color: #434b58;
  font-weight: 500;
  font-size: 1rem;
  line-height: 1.429rem;
}
.checkbox-toggle {
  position: relative;
  width: 1.7147rem;
  height: 1.7147rem;
  background-color: $color_white;
  border: 1px solid #c2c6ce;
  border-radius: 2px;
  transition: all 0.3s;
  @include ir();
  &.toggle {
    background-color: $color_primary;
    border-color: transparent;
    &:after {
      position: absolute;
      top: 0.3573rem;
      left: 0.3573rem;
      width: 0.8569rem;
      height: 0.3576rem;
      border-bottom: solid 2px $color_white;
      border-left: solid 2px$color_white;
      transform: rotate(-45deg);
      content: '';
    }
  }
}
</style>
