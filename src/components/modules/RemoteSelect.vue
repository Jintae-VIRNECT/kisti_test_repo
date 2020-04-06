<template>
  <popover
    placement="bottom"
    trigger="click"
    :width="300"
    popperClass="select-options"
    :style="style"
    v-on:show="show = true"
    v-on:hide="show = false"
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
  min-width: 300px;
  min-height: 36px;
  padding: 9px 20px;
  color: #fff;
  line-height: 20px;
  text-align: left;
  // background: url(~assets/image/ic-select-collapse.svg) no-repeat 95% 50%;
  background-color: #111012;
  border: solid 1px #363638;
  border-radius: 3px;
  &::after {
    position: absolute;
    top: 6px;
    right: 15px;
    width: 24px;
    height: 24px;
    background: url(~assets/image/ic-select-collapse.svg) no-repeat 50%;
    transform: rotate(0deg);
    transition: transform 0.3s;
    content: '';
  }
  &.active {
    outline: none;
    outline-offset: 0;
    &::after {
      transform: rotate(-180deg);
      content: '';
    }
  }
}
.popover.select-options {
  margin-top: -2px;
  background-color: #111012;
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
  padding: 10px 20px;
  color: #fff;
  text-align: left;
  opacity: 0.5;
  // &:last-child {
  //   border-bottom: none;
  // }
  &.active {
    opacity: 0.76;
  }
  &:hover {
    opacity: 0.76;
  }
}
</style>
