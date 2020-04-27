<template>
  <figure class="inputrow">
    <p class="inputrow-title valid" v-if="valid" :class="{ required }">
      {{ validMessage }}
    </p>
    <p class="inputrow-title" v-else :class="{ required }">{{ title }}</p>
    <input
      v-if="type === 'text'"
      class="inputrow-input input"
      type="text"
      :placeholder="placeholder"
      v-model="inputText"
      :maxlength="count"
      @focusout="$emit('focusOut')"
    />
    <textarea
      v-else-if="type === 'textarea'"
      ref="inputTextarea"
      class="inputrow-input textarea"
      type="text"
      :placeholder="placeholder"
      v-model="inputText"
      :maxlength="count"
    />
    <slot v-else></slot>
    <span class="inputrow-length" v-if="showCount">{{
      `${inputText.length}/${count}`
    }}</span>
  </figure>
</template>

<script>
import * as regexp from 'utils/regexp.js'
export default {
  name: 'InputRow',
  data() {
    return {
      inputText: '',
    }
  },
  props: {
    value: {
      type: String,
      default: '',
    },
    type: {
      type: String,
      default: 'input',
    },
    title: {
      type: String,
      default: '협업 이름',
    },
    placeholder: {
      type: String,
      default: '그룹이름을 입력해 주세요.',
    },
    validate: {
      type: String,
      default: null,
    },
    validMessage: {
      type: String,
      default: '',
    },
    showCount: {
      type: Boolean,
      default: false,
    },
    count: {
      type: Number,
      default: 20,
    },
    required: {
      type: Boolean,
      default: false,
    },
    valid: {
      type: Boolean,
      default: false,
    },
  },
  computed: {},
  watch: {
    inputText(text) {
      if (this.validate) {
        const invalid = regexp[this.validate](text)
        if (!invalid) {
          this.$emit('update:valid', true)
        } else {
          this.$emit('update:valid', false)
        }
      }
      this.$emit('update:value', text)
    },
    value(val) {
      if (val !== this.inputText) {
        this.inputText = val
      }
    },
  },

  /* Lifecycles */
  mounted() {
    if (this.value && this.value.length > 0) {
      this.inputText = this.value
    }
  },
}
</script>
<style lang="scss" scoped>
@import '~assets/style/vars';

.inputrow {
  position: relative;
  width: 100%;
  margin-top: 2.143em;
}
.inputrow-title {
  position: relative;
  width: fit-content;
  margin-bottom: 0.571em;
  color: rgba(#d2d2d2, 0.5);
  font-size: 0.929em;
  &.required::after {
    position: absolute;
    top: 0px;
    right: -0.714em;
    width: 0.357em;
    height: 0.357em;
    background-color: $color_primary;
    border-radius: 50%;
    content: '';
  }
  &.valid {
    color: #ff757b;
    &.required::after {
      display: none;
    }
  }
}

.inputrow-input {
  width: calc(100% - 2.857em);
  min-height: 1.429em;
  padding: 0.571em;
  padding: 1em 1.286em;
  color: #fff;
  font-size: 1em;
  background-color: #1a1a1b;
  border: solid 1px #303030;
  border-radius: 3px;
  &::placeholder {
    color: rgba(#979fb0, 0.7);
  }
  &:hover {
    border: solid 1px #585858;
  }
  &:active,
  &:focus {
    border: solid 1px $color_primary;
  }
  &.textarea {
    height: 4.286em;
    overflow-y: hidden;
    resize: none;
  }
}
.inputrow-length {
  position: absolute;
  right: 0;
  bottom: -1.571em;
  color: rgba(#979fb0, 0.7);
  font-size: 0.857em;
}
</style>
