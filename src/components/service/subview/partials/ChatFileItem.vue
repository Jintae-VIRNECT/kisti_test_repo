<template>
  <div class="chat-file-item" :class="{ active: checked, invalid: !isValid }">
    <div class="chat-file-item--wrapper">
      <div class="chat-file-item--icon" :class="ext"></div>

      <div class="chat-file-item__body">
        <div class="chat-file-item__center">
          <div
            class="chat-file-item__center--file-name"
            :class="{ invalid: !isValid }"
          >
            {{ fileName }}
          </div>
        </div>

        <div class="chat-file-item__sub">
          <span
            class="chat-file-item__sub--valid-text"
            :class="{ invalid: !isValid }"
          >
            유효기간
            {{ validDate }}
          </span>
          <span class="chat-file-item__sub--file-size">{{ fileSize }}</span>
        </div>
      </div>
    </div>
    <div
      v-if="isValid"
      class="chat-file-item--check-outer"
      :class="{ active: checked }"
      @click="toggle"
    >
      <div
        class="chat-file-item--check-inner"
        :class="{ active: checked }"
      ></div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ChatFileItem',
  data() {
    return {
      checked: false,
    }
  },
  props: {
    id: {
      type: [String, Number],
      default: null,
    },
    fileName: {
      type: String,
      default: '',
    },
    ext: {
      type: String,
      default: '',
    },
    validDate: {
      type: String,
      default: '',
    },
    fileSize: {
      type: String,
      default: '',
    },
    isValid: {
      type: Boolean,
      default: true,
    },
  },
  methods: {
    toggle() {
      this.checked = !this.checked
      if (this.checked) {
        this.$eventBus.$emit('chatfile::selected', this.id)
      } else {
        this.$eventBus.$emit('chatfile::unselected', this.id)
      }
    },
    release() {
      this.checked = false
    },
  },
  mounted() {
    this.$eventBus.$on('chatfile::release', this.release)
  },
  beforeDestroy() {
    this.$eventBus.$off('chatfile::release')
  },
}
</script>

<style></style>
