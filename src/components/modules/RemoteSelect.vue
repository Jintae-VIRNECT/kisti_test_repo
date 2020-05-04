<template>
  <popover
    ref="selectPopover"
    placement="bottom"
    :placementReverse="true"
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
    init() {
      if (!this.defaultValue || this.defaultValue.length < 1) {
        if (this.options.length > 0) {
          this.selected = this.options[0]
        }
      } else {
        const selectVal = this.options.find(
          option => option[this.value] === this.defaultValue,
        )

        if (selectVal !== undefined) {
          this.selected = selectVal
        }
      }
    },
    select(option) {
      this.selected = option
      this.$emit('changeValue', option)
      this.$eventBus.$emit('popover:close')
    },
  },
  watch: {
    options: {
      handler() {
        this.init()
      },
      deep: true,
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
  min-width: 21.429rem;
  min-height: 2.857rem;
  padding: 0.643em 1.429rem;
  color: rgba($color_text, 0.76);
  line-height: 1.429rem;
  text-align: left;
  background-color: $color_darkgray;
  border: solid 1px #363638;
  border-radius: 3px;
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
    background-color: $color_darkgray;
  }
  &.active {
    outline: none;
    outline-offset: 0;
    &::after {
      background: url(~assets/image/ic_dropdown.svg) no-repeat 50%;
      transform: rotate(-180deg);
      content: '';
    }
  }
}
.popover.select-options {
  min-height: 2.143rem;
  margin-top: -0.286rem;
  background-color: $color_darkgray;
  border: solid 1px #363638;
  border-top: none;
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
}
.select-option {
  width: 100%;
  padding: 0.571em 1.429rem;
  color: rgba($color_text, 0.5);
  line-height: 1.429rem;
  text-align: left;
  &:last-child {
    padding-bottom: 1.143rem;
  }
  &.active {
    color: rgba($color_text, 0.76);
  }
  &:hover {
    color: rgba($color_text, 0.76);
  }
}
</style>
