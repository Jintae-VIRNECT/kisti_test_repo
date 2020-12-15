<template>
  <popover
    ref="selectPopover"
    placement="bottom"
    :placementReverse="true"
    trigger="click"
    popperClass="select-options"
    :fullwidth="true"
    :scrollHide="true"
    :disabled="disabled"
    :shadow="shadow"
    :topOffset="10"
    @visible="visible => (show = visible)"
  >
    <button
      slot="reference"
      class="select-label"
      :class="{ active: show, disabled: disabled, greyarrow: greyarrow }"
    >
      {{ selected[text] }}
    </button>
    <div class="select-optionbox">
      <button
        class="select-option"
        v-for="(option, idx) of options"
        :key="idx"
        :class="{ active: selected.value === option[value] }"
        @click="select(option)"
      >
        {{ option[text] }}
      </button>
    </div>
  </popover>
</template>

<script>
import Popover from 'Popover'
export default {
  name: 'Select',
  components: {
    Popover,
  },
  props: {
    disabled: {
      type: Boolean,
      default: false,
    },
    selectedValue: {
      type: String,
      default: '',
    },
    options: {
      type: Array,
      default: () => {
        return []
      },
    },
    value: {
      type: String,
      default: 'value',
    },
    text: {
      type: String,
      default: 'text',
    },
    greyarrow: {
      type: Boolean,
      default: false,
    },
    shadow: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {
      show: false,
    }
  },
  computed: {
    selected() {
      const idx = this.options.findIndex(
        option => option[this.value] === this.selectedValue,
      )
      if (idx < 0) {
        return {
          [this.value]: '',
          [this.text]: '',
        }
      } else {
        return this.options[idx]
      }
    },
  },
  watch: {
    'options.length': 'init',
  },
  methods: {
    init() {
      if (this.options.length === 0) return
      if (!this.selectedValue || this.selectedValue.length < 1) {
        this.$emit('update:selectedValue', this.options[0][this.value])
      } else {
        const idx = this.options.findIndex(
          option => option[this.value] === this.selectedValue,
        )

        if (idx > -1) {
          this.$emit('update:selectedValue', this.options[idx][this.value])
        } else {
          this.$emit('update:selectedValue', this.options[0][this.value])
        }
      }
    },
    select(option) {
      this.$emit('update:selectedValue', option[this.value])
      this.$eventBus.$emit('popover:close')
    },
  },
  /* Lifecycles */
  mounted() {
    this.init()
  },
}
</script>

<style lang="scss">
@import '~assets/style/mixin';
.select-label {
  position: relative;
  width: 100%;
  min-width: 10.1429rem;
  min-height: 2.857rem;
  padding: 0.643em 1.429rem;
  overflow: hidden;
  color: #757f91;
  font-weight: 500;
  font-size: 1.1429rem;
  line-height: 1.429rem;
  white-space: nowrap;
  text-align: left;
  text-overflow: ellipsis;
  background-color: $color_white;
  border: 1px solid #c2c6ce;
  border-radius: 4px;
  transition: all 0.3s;
  &::after {
    position: absolute;
    top: 0.429rem;
    right: 1.071rem;
    width: 1.714rem;
    height: 1.714rem;
    margin-right: 0.143rem;
    background: url(~assets/image/ic_dropdown.svg) no-repeat 50%;
    transform: rotate(0deg);
    transition: transform 0.3s;
    content: '';
  }
  &:hover,
  &:active,
  &:focus {
    background-color: $color_white;
  }
  &.active {
    outline: none;
    outline-offset: 0;
    &::after {
      transform: rotate(-180deg);
      content: '';
    }
  }
  &.disabled {
    color: rgba($color_text, 0.2);
    border-color: $color_darkgray_1000;
    outline-offset: 0;
    cursor: default;
    &::after {
      opacity: 0.2;
    }
  }
  &.greyarrow {
    &::after {
      top: 0.7147rem;
      right: 1.071rem;
      background: url(~assets/image/ic_dropdown_nobg.svg) no-repeat 50% #a4a4a4;
      border-radius: 4px;
    }
  }
}
.popover.select-options {
  min-width: 10.1429rem;
  min-height: 2.143rem;
  margin-top: -0.286rem;
  background-color: $color_white;
  border: solid 1px #c2c6ce;
  border-radius: 3px;
  > .popover--body {
    padding: 0;
  }
  &.reverse {
    margin-top: 0.286rem;
    border-top: solid 1px #363638;
    border-bottom: none;
    > .popover--body .select-optionbox .select-option {
      &:first-child {
        padding-top: 1.143rem;
      }
      &:last-child {
        padding-bottom: 0.571rem;
      }
    }
  }
}
.select-optionbox {
  position: relative;
  padding: 0.4286rem 0;
}
.select-option {
  width: 100%;
  padding: 0.571em 1.429rem;
  color: #757f91;
  font-weight: 500;
  font-size: 1.1429rem;
  line-height: 1.429rem;
  text-align: left;
  &:last-child {
    padding-bottom: 1.143rem;
  }
  &.active {
    color: #0b1f48;
    text-decoration: underline;
    background-color: #e3e7ed;
  }
  &:hover {
    background-color: #e3e7ed;
  }
}
</style>
