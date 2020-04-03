<template>
  <popover
    placement="bottom"
    trigger="click"
    :width="300"
    popperClass="select-options"
    :style="style"
  >
    <button slot="reference" class="select-label">{{ selected[text] }}</button>
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
  min-width: 300px;
  min-height: 36px;
  padding: 9px 16px;
  color: #fff;
  line-height: 20px;
  text-align: left;
  background: url(~assets/image/ic-select-collapse.svg) no-repeat 95% 50%;
  background-color: #111012;
  border: solid 1px #363638;
  border-radius: 3px;
  &:focus {
    outline: none;
    outline-offset: 0;
  }
}
.popover.select-options {
  margin-top: 3px;
  background-color: #111012;
  border: solid 1px #363638;
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
  padding: 10px;
  color: #fff;
  text-align: left;
  border-bottom: solid 1px #363638;
  &:last-child {
    border-bottom: none;
  }
  &.active {
    background-color: #252527;
  }
  &:hover {
    background-color: #363638;
  }
}
</style>
