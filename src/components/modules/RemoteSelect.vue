<template>
  <popover
    placement="bottom"
    trigger="click"
    :width="300"
    popperClass="select-options"
    :style="style"
  >
    <button slot="reference" class="select-label">{{ selectedValue }}</button>
    <div class="select-optionbox">
      <button
        class="select-option"
        v-for="(option, idx) of options"
        :key="idx"
        :class="{ active: selectedValue === option[text] }"
      >
        {{ option[text] }}
      </button>
    </div>
  </popover>
  <!-- <select class="select" v-model="selectedValue">
    <option
      class="select-option"
      v-for="(option, idx) of options"
      :value="option[value]"
      :key="idx"
      >{{ option[text] }}
    </option>
  </select> -->
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
      selectedValue: '',
      style: {
        // backgroundColor: '#111012',
      },
    }
  },
  props: {
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
    defaultValue: {
      type: String,
    },
  },
  computed: {},

  /* Lifecycles */
  mounted() {},
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
}
</style>
