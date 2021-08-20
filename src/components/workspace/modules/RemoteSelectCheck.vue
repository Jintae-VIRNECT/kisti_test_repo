<template>
  <popover
    ref="selectPopover"
    placement="bottom-end"
    :placementReverse="true"
    trigger="click"
    popperClass="select-options"
    :disabled="disabled"
    @visible="visible => (show = visible)"
    :targetElement="targetElement"
    :useTopMargin="true"
    :scrollHide="true"
    width="22.2143rem"
  >
    <button
      slot="reference"
      class="select-label"
      :class="{ active: show, disabled: disabled }"
    >
      <span>{{ selected[text] }}</span>
    </button>
    <div class="select-optionbox">
      <button
        class="select-option"
        v-for="(option, idx) of options"
        :key="idx"
        :class="{ active: selected[value] === option[value] }"
        @click="select(option)"
      >
        <img
          v-if="selected[value] === option[value]"
          class="select-option--check"
          :src="require('assets/image/ic_check_select_on.svg')"
        />
        <img
          v-else
          class="select-option--check"
          :src="require('assets/image/ic_check_select_off.svg')"
        />
        <p class="select-option--text">{{ option[text] }}</p>

        <span class="select-option--sub-text">{{ option[subText] }}</span>
      </button>
    </div>
  </popover>
</template>

<script>
import Popover from 'Popover'
// import Scroller from 'Scroller'
export default {
  name: 'RemoteSelectCheck',
  components: {
    Popover,
    // Scroller,
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
    targetElement: {
      type: String,
    },
    subText: {
      type: String,
      default: 'subText',
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
  min-width: 21.429rem;
  min-height: 2.857rem;
  padding: 0.643em 1.429rem;
  overflow: hidden;
  color: rgba($color_text, 0.76);
  line-height: 1.429rem;
  white-space: nowrap;
  text-align: left;
  text-overflow: ellipsis;
  background-color: $color_darkgray;
  border: solid 1px #363638;
  border-radius: 3px;
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
    background-color: $color_darkgray;
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
    border-color: #000000;
    outline-offset: 0;
    cursor: default;
    &::after {
      opacity: 0.2;
    }
  }
  > span {
    width: 100%;
    padding-right: 1.429rem;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }
}
.popover.select-options {
  min-height: 2.143rem;
  margin-top: -0.286rem;
  background: rgb(30, 30, 32);
  background-color: $color_darkgray;
  border: 1px solid rgb(48, 48, 48);
  border-radius: 2px;
  box-shadow: 0px 2px 4px 0px rgba(0, 0, 0, 0.5);
  > .popover--body {
    padding: 0;
    background: rgb(30, 30, 32);
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
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
  padding: 0.571em 1.429rem;
  color: rgba($color_text, 0.5);
  line-height: 1.429rem;
  text-align: left;

  &:first-child {
    padding-top: 1.0001rem;
  }
  &.active {
    color: rgba($color_text, 0.76);
  }
  &:hover {
    color: rgba($color_text, 0.76);
    background-color: #2b2b2d;
  }
}

.select-option--text {
  width: 80%;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.select-option--sub-text {
  position: absolute;
  right: 1.0714rem;
  color: rgb(255, 255, 255);

  font-size: 0.9286rem;
  opacity: 0.5;
}
.select-option--check {
  padding-right: 0.9286rem;
}
</style>
