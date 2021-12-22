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
            <!-- {{ $t('service.file_validdatae') + validDate }} -->
            <span>{{ fileInfo.exp }}</span>
            <em>{{ fileSize }}</em>
          </p>
        </div>
      </div>
    </button>
  </article>
</template>

<script>
import { checkFileType } from 'utils/fileTypes'
import { fileSizeFilter } from 'utils/file'

export default {
  name: 'ChatFileItem',
  props: {
    file: {
      type: Object,
      default: () => {
        return {}
      },
    },
    checked: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    fileSize() {
      return fileSizeFilter(this.file.size)
    },
    isInvalid() {
      return this.file.expired
    },
    // validDate() {
    //   return this.$dayjs(this.file.expirationDate).format('YYYY.MM.DD')
    // },
    icon() {
      const extension = checkFileType({
        name: this.file.name,
        type: this.file.contentType,
      })
      const extensionMap = {
        threed: require('assets/image/call/chat/ic_3d_w.svg'),
        zip: require('assets/image/call/chat/ic_zip_w.svg'),
        pdf: require('assets/image/call/chat/ic_pdf_w.svg'),
        audio: require('assets/image/call/chat/ic_audio_w.svg'),
        video: require('assets/image/call/chat/ic_video_w.svg'),
        image: require('assets/image/call/chat/ic_image_w.svg'),
        doc: require('assets/image/call/chat/ic_ms_w.svg'),
      }

      if (Object.keys(extensionMap).includes(extension)) {
        return extensionMap[extension]
      } else {
        return require('assets/image/call/chat/ic_file_w.svg')
      }
    },
    fileInfo() {
      const idx = this.file.name.lastIndexOf('.')
      return {
        exp: this.file.name.toLowerCase().substr(idx + 1),
        name: this.file.name.substr(0, idx),
      }
    },
  },
}
</script>

<style></style>
