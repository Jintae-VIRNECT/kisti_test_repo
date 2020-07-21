<template>
  <article>
    <button
      class="chat-file-item"
      :class="{ active: checked, invalid: !isValid }"
      @click="toggle"
    >
      <div class="chat-file-item--wrapper">
        <img class="chat-file-item--icon" :src="icon" />

        <div class="chat-file-item__body">
          <div class="chat-file-item__center">
            <p
              class="chat-file-item__center--file-name"
              :class="{ invalid: !isValid }"
            >
              {{ fileName }}
            </p>
          </div>

          <div class="chat-file-item__sub">
            <p
              class="chat-file-item__sub--valid-text"
              :class="{ invalid: !isValid }"
            >
              유효기간
              {{ validDate }}
              <span>{{ fileSize }}</span>
            </p>
          </div>
        </div>
      </div>
      <div
        v-if="isValid"
        class="chat-file-item--check-outer"
        :class="{ active: checked }"
      ></div>
    </button>
  </article>
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
  computed: {
    icon() {
      switch (this.ext) {
        case 'pdf':
          return require('assets/image/call/chat/ic_chat_pdf_w.svg')
        case 'video':
          return require('assets/image/call/chat/ic_chat_video_w.svg')
        case 'mp3':
          return require('assets/image/call/chat/ic_chat_mp3_w.svg')
        case 'txt':
          return require('assets/image/call/chat/ic_chat_file_w.svg')
        default:
          return require('assets/image/call/chat/ic_chat_jpgpng_w.svg')
      }
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
