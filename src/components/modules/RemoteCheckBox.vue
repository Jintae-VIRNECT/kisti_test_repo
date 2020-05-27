<template>
  <div class="checkbox">
    <div
      class="checkbox__box"
      :class="{ active: checked }"
      @click="toggleCheckBox"
    >
      <img v-if="checked" src="~assets/image/mdpi_icon_check.svg" />
    </div>
    <label :value="value" class="checkbox__label">{{ text }}</label>
  </div>
</template>

<script>
export default {
  name: 'RemoteRadio',
  computed: {},
  data() {
    return {
      checked: false,
    }
  },
  watch: {
    checked() {
      if (this.checked) {
        this.$emit('toggle', this.value)
      }
    },
  },
  props: {
    text: {
      type: String,
      default: '',
    },
    value: {
      default: null,
    },
    default: {
      type: Boolean,
      default: false,
    },
  },
  methods: {
    toggleCheckBox() {
      this.checked = !this.checked
    },
  },

  /* Lifecycles */
  mounted() {
    if (this.default) {
      this.checked = true
    }
  },
}
</script>

<style lang="scss">
@import '~assets/style/mixin';
.checkbox {
  display: flex;
  align-items: center;
}

.checkbox__box {
  width: 1.25rem;
  height: 1.25rem;
  margin-right: 0.688rem;
  border: 1px solid $color_sub_border;
  border-radius: 2px;
  &.active {
    background-color: $color_primary;
    border: none;
    border-radius: 2px;
  }
}

.checkbox__label {
  color: $color_white;
  font-size: 0.813rem;
}
</style>
