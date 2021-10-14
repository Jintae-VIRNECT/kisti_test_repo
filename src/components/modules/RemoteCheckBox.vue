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
  name: 'RemoteCheckBox',
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
  width: 23.286em;
  padding: 0.571rem;
  background-color: #212125;
  border: solid 1px #171718;
  border-radius: 3px;
  cursor: pointer;
  transition: all 0.3s;
  &.toggle {
    background-color: $color_primary;
  }
}
.checkbox-text {
  margin: auto;
  margin-left: 0.714rem;
  color: rgba($color_white, 0.6);
  font-weight: 400;
  line-height: 1.429rem;
}

//스페인어 길이 대응
body {
  &:lang(es) {
    .checkbox-text {
      font-size: 0.8571rem !important;
      line-height: 0.7857rem;
    }
  }
}

.checkbox-toggle {
  position: relative;
  width: 1.429rem;
  height: 1.429rem;
  // background-color: rgb(209, 77, 77);
  background-color: #17171a;
  border: solid 1px #333335;
  border-radius: 2px;
  transition: all 0.3s;
  @include ir();
  &.toggle {
    background-color: #fff;
    border-color: transparent;
    &:after {
      position: absolute;
      top: 0.143rem;
      left: 0.143rem;
      width: 0.714rem;
      height: 0.429rem;
      border-bottom: solid 3px $color_primary;
      border-left: solid 3px $color_primary;
      transform: rotate(-45deg);
      content: '';
    }
  }
}
</style>
