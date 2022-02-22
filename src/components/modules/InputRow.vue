<template>
  <figure class="inputrow">
    <p
      v-if="titlePosition === 'top'"
      class="inputrow-title"
      :class="{ required, valid }"
    >
      <span v-if="valid"> {{ validMessage }} </span>
      {{ title ? title : $t('workspace.remote_name') }}
    </p>
    <input
      v-if="type === 'text'"
      class="inputrow-input input"
      type="text"
      :placeholder="
        placeholder ? placeholder : $t('workspace.create_remote_name_input')
      "
      v-model="inputText"
      @input="inputChange"
      :maxlength="count"
      @focusout="$emit('focusOut')"
    />
    <textarea
      v-else-if="type === 'textarea'"
      ref="inputTextarea"
      class="inputrow-input textarea"
      type="text"
      :placeholder="
        placeholder ? placeholder : $t('workspace.create_remote_name_input')
      "
      v-model="inputText"
      @input="inputChange"
      :maxlength="count"
    />
    <p
      v-if="titlePosition === 'bottom'"
      class="inputrow-title"
      :class="{ required, valid }"
    >
      <span v-if="valid"> {{ validMessage }} </span>
    </p>
    <slot v-else></slot>
    <span class="inputrow-length" :class="countPosition" v-if="showCount">{{
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
      default: null,
    },
    titlePosition: {
      type: String,
      default: 'top',
    },
    placeholder: {
      type: String,
      default: null,
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
    countPosition: {
      type: String,
      default: null,
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
  methods: {
    inputChange(event) {
      if (event.target.value.trim() === '') {
        this.inputText = ''
      } else {
        this.inputText = event.target.value
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
  & + & {
    margin-top: 2.143em;
  }
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
    color: transparent;
    text-indent: -99px;
    &.required::after {
      display: none;
    }
  }
  > span {
    position: absolute;
    top: 0;
    left: 0;
    color: #ff757b;
    white-space: nowrap;
    text-indent: 0;
    // background: ;
  }
}

.inputrow-input {
  width: calc(100% - 2.857em);
  font-weight: normal;
  min-height: 1.429em;
  padding: 0.571em;
  padding: 1em 1.286em;
  color: rgb(38, 38, 38);
  font-size: 1em;
  background-color: rgb(236, 239, 242);
  border: solid 1px #c0c6d1;
  border-radius: 3px;
  &::placeholder {
    color: rgba(#979fb0, 0.7);
  }
  &:hover {
    border: solid 1px $color_primary;
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
  color: #757f91;
  font-size: 0.857em;

  &.mid-right {
    top: 31%;
    right: 1.0714rem;
    font-size: 0.857em;
  }
}
</style>
