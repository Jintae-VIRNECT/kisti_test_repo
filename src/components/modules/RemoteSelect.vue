<template>
  <popover
    ref="selectPopover"
    placement="bottom"
    trigger="click"
    popperClass="select-options"
    :style="style"
    :fullwidth="true"
    :scrollHide="true"
    @visible="visible => (show = visible)"
  >
    <button slot="reference" class="select-label" :class="{ active: show }">
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
  name: 'RemoteSelect',
  components: {
    Popover,
  },
  data() {
    return {
      selected: {},
      style: {
        // backgroundColor: '#111012',
      },
      show: false,
    }
  },
  props: {
    defaultValue: {
      type: String,
      default: '',
    },
    options: {
      type: Array,
    },
    value: {
      type: String,
      default: 'value',
    },
    text: {
      type: String,
      default: 'text',
    },
  },
  methods: {
    select(option) {
      this.selected = option
      this.$emit('changeValue', option)
      this.$eventBus.$emit('popover:close')
    },
  },

  /* Lifecycles */
  mounted() {
    if (!this.defaultValue || this.defaultValue.length < 1) {
      if (this.options.length > 0) {
        this.selected = this.options[0]
      }
    } else {
      this.selected = this.options.find(
        option => option.value === this.defaultValue,
      )
    }
  },
}
</script>

<style lang="scss">
@import '~assets/style/mixin';
.select-label {
  position: relative;
  width: 100%;
  min-width: 300px;
  min-height: 40px;
  padding: 9px 20px;
  color: rgba($color_text, 0.76);
  line-height: 20px;
  text-align: left;
  // background: url(~assets/image/ic-select-collapse.svg) no-repeat 95% 50%;
  background-color: $color_darkgray;
  border: solid 1px #363638;
  border-radius: 3px;
  &::after {
    position: absolute;
    top: 6px;
    right: 15px;
    width: 24px;
    height: 24px;
    margin-right: 2px;
    background: url(~assets/image/ic-select-dropdown.svg) no-repeat 50%;
    transform: rotate(0deg);
    transition: transform 0.3s;
    content: '';
  }
  &:hover,
  &:active,
  &:focus {
    background-color: $color_darkgray;
  }
  &.active {
    outline: none;
    outline-offset: 0;
    &::after {
      background: url(~assets/image/ic-select-dropdown.svg) no-repeat 50%;
      transform: rotate(-180deg);
      content: '';
    }
  }
}
.popover.select-options {
  min-height: 30px;
  margin-top: -4px;
  background-color: $color_darkgray;
  border: solid 1px #363638;
  border-top: none;
  border-radius: 3px;
  > .popover--body {
    padding: 0;
  }
}
.select-optionbox {
  position: relative;
}
.select-option {
  width: 100%;
  padding: 8px 20px;
  color: rgba($color_text, 0.5);
  line-height: 20px;
  text-align: left;
  &:last-child {
    padding-bottom: 16px;
  }
  &.active {
    color: rgba($color_text, 0.76);
  }
  &:hover {
    color: rgba($color_text, 0.76);
  }
}
</style>
