<template>
  <article>
    <button
      class="chat-file-item"
      :class="{ active: checked, invalid: isInvalid }"
      @click="$emit('toggle')"
    >
      <div class="chat-file-item__wrapper">
        <img class="chat-file-item__icon" :src="icon" />

        <div class="chat-file-item__body">
          <p class="chat-file-item__name" :class="{ invalid: isInvalid }">
            {{ fileInfo.name }}
          </p>

          <p class="chat-file-item__valid" :class="{ invalid: isInvalid }">
            {{ $t('service.file_validdatae') + validDate }}
            <em>{{ fileSize }}</em>
          </p>
        </div>
      </div>
    </button>
  </article>
</template>

<script>
export default {
  name: 'ChatFileItem',
  props: {
    fileInfo: {
      type: Object,
      default: () => {
        return {}
      },
    },
    ext: {
      type: String,
      default: '',
    },
    checked: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    fileSize() {
      if (this.fileInfo.size > 1024 * 1024) {
        return parseFloat(this.fileInfo.size / 1024 / 1024).toFixed(1) + 'MB'
      }
      return parseFloat(this.fileInfo.size / 1024).toFixed(1) + 'KB'
    },
    isInvalid() {
      return false
    },
    validDate() {
      return this.$dayjs(this.fileInfo.createdDate).format('YYYY.MM.DD')
    },
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
}
</script>

<style></style>
