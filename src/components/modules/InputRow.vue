<template>
  <figure class="inputrow" :class="{ box: box === true }">
    <p class="inputrow-title">{{ title }}</p>
    <input
      v-if="type === 'text'"
      class="inputrow-input input"
      type="text"
      :placeholder="placeholder"
      v-model="inputText"
      :maxlength="count"
    />
    <textarea
      v-else
      ref="inputTextarea"
      class="inputrow-input textarea"
      type="text"
      :placeholder="placeholder"
      v-model="inputText"
      @keyup="cmaTextareaSize(30)"
      :maxlength="count"
    />
    <span class="inputrow-length" v-if="showCount">{{
      `${inputText.length}/${count}`
    }}</span>
  </figure>
</template>

<script>
export default {
  name: 'InputRow',
  data() {
    return {
      inputText: '',
    }
  },
  props: {
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
    regexp: {
      type: String,
      default: null,
    },
    box: {
      type: Boolean,
      default: false,
    },
    showCount: {
      type: Boolean,
      default: false,
    },
    count: {
      type: Number,
      default: 20,
    },
  },
  computed: {},
  watch: {
    inputText(text) {
      this.$emit('update:value', text)
      if (this.type === 'textarea' && !this.box) {
        this.cmaTextareaSize(30)
      }
    },
  },
  methods: {
    cmaTextareaSize(bsize) {
      if (this.box) return
      let sTextarea = this.$refs['inputTextarea']
      sTextarea.style.height = 'fit-content'
      let csize =
        sTextarea.scrollHeight >= bsize
          ? sTextarea.scrollHeight - 16 + 'px'
          : bsize - 16 + 'px'
      sTextarea.style.height = csize
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>
<style lang="scss" scoped>
.inputrow {
  position: relative;
  width: 100%;
  margin-top: 30px;
}
.inputrow-title {
  margin-bottom: 8px;
  color: #d2d2d2;
  font-weight: 500;
  font-size: 13px;
}

.inputrow-input {
  width: calc(100% - 16px);
  min-height: 20px;
  padding: 8px;
  color: #fff;
  background: transparent;
  border: none;
  border-bottom: solid 1px rgba(#fff, 0.18);
  &::placeholder {
    color: rgba(#979fb0, 0.7);
  }
  &.textarea {
    height: fit-content;
    overflow-y: hidden;
    resize: none;
  }
}
.inputrow-length {
  position: absolute;
  right: 0;
  bottom: -22px;
  color: rgba(#979fb0, 0.7);
  font-size: 12px;
}
.inputrow.box {
  .inputrow-input {
    width: calc(100% - 40px);
    padding: 16px 20px;
    background-color: #111012;
    border: solid 1px #282828;
    border-radius: 3px;
    &.textarea {
      height: 60px;
    }
    &:hover {
      border: solid 1px #585858;
    }
    &:active,
    &:focus {
      border: solid 1px #0f75f5;
    }
  }
}
</style>
