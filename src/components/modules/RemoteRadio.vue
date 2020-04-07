<template>
  <div class="radio-group">
    <label
      class="radio-option"
      :class="{ active: option.value === picked }"
      v-for="option of options"
      :key="option[value]"
    >
      <span
        class="radio-option__input"
        :class="{ active: option.value === picked }"
      >
        <input type="radio" :value="option[value]" v-model="picked" />
      </span>
      <span class="radio-option__label">{{ option[text] }}</span>
      <span v-if="option.imgSrc" class="radio-option__image">
        <img :src="option.imgSrc" />
      </span>
    </label>
  </div>
</template>

<script>
export default {
  name: 'RemoteRadio',
  computed: {},
  data() {
    return {
      picked: '',
    }
  },
  watch: {
    picked() {
      this.$emit('update:selectedOption', this.picked)
    },
  },
  props: {
    selectedOption: {
      type: String,
    },
    options: {
      type: Array,
      default: () => {
        return []
      },
    },
    text: {
      type: String,
      default: 'text',
    },
    value: {
      type: String,
      default: 'value',
    },
  },
  methods: {},

  /* Lifecycles */
  mounted() {
    if (this.selectedOption) {
      this.picked = this.selectedOption
    }
  },
}
</script>

<style lang="scss">
@import '~assets/style/mixin';
.radio {
  color: #fff;
}
.radio-group {
  display: flex;
  flex-direction: column;
}
.radio-option {
  position: relative;
  display: flex;
  padding: 12px 0;
  color: rgba(#fff, 0.8);
  cursor: pointer;
  &.active,
  &:hover,
  &:active {
    color: #fff;
  }
}
.radio-option__input {
  position: relative;
  display: inline-block;
  box-sizing: border-box;
  width: 20px;
  height: 20px;
  margin-right: 13px;
  border: solid 1px #979797;
  border-radius: 50%;
  cursor: pointer;
  &.active {
    border-color: #0f75f5;
    &::before {
      position: absolute;
      top: 50%;
      left: 50%;
      width: 12px;
      height: 12px;
      margin: auto;
      background-color: #0f75f5;
      border-radius: 50%;
      transform: translate(-50%, -50%);
      content: '';
    }
  }
}
.radio-option__label {
  line-height: 20px;
}
.radio-option__image {
  position: absolute;
  top: 50%;
  right: 0;
  line-height: 0;
  transform: translateY(-50%);
}
</style>
