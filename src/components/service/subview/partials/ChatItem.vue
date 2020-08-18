<template>
  <li class="chat-item" :class="[chat.type, { 'file-share': isFile }]">
    <profile
      class="chat-item__profile"
      v-if="!hideProfile"
      :image="chat.profile"
    ></profile>
    <div class="chat-item__body" :class="[chat.type, { hidden: hideProfile }]">
      <div class="chat-item__body--chatbox">
        <span class="chat-item__body--name" v-if="!hideProfile">{{
          chat.name
        }}</span>
        <div v-if="isFile" class="chat-item__file">
          <div class="chat-item__file--wrapper">
            <div class="chat-item__file--icon" :class="extension"></div>
            <div class="chat-item__file--name">
              {{ chat.file[0].fileName }}
            </div>
          </div>
          <p class="chat-item__file--size">{{ fileSize }}</p>
        </div>
        <p
          class="chat-item__body--text"
          :class="subClass"
          v-html="chatText"
        ></p>
        <button v-if="isFile" class="chat-item__file--button" @click="download">
          <span class="button-text">{{ $t('button.download') }}</span>
        </button>
      </div>
      <span v-if="!hideTime" class="chat-item__body--time">{{
        $dayjs(chat.date).format('A hh:mm')
      }}</span>
    </div>
  </li>
</template>

<script>
import Profile from 'Profile'
import FileSaver from 'file-saver'
import linkifyHtml from 'linkifyjs/html'
export default {
  name: 'ChatItem',
  components: {
    Profile,
  },
  data() {
    return {}
  },
  props: {
    beforeChat: Object,
    afterChat: Object,
    chat: Object,
  },
  computed: {
    isFile() {
      if (this.chat.file && this.chat.file.length > 0) {
        return true
      } else {
        return false
      }
    },
    hideTime() {
      if (this.afterChat === null) {
        return false
      }

      if (this.afterChat.type !== this.chat.type) {
        return false
      }

      if (
        this.$dayjs(this.afterChat.date).format('A hh:mm') !==
        this.$dayjs(this.chat.date).format('A hh:mm')
      ) {
        return false
      }

      if (
        this.chat.connectionId !== null &&
        this.afterChat.connectionId !== this.chat.connectionId
      ) {
        return false
      }

      return true
    },
    hideProfile() {
      if ('opponent' !== this.chat.type) return true
      if (this.beforeChat === null) {
        return false
      }
      if (
        this.$dayjs(this.beforeChat.date).format('A hh:mm') !==
        this.$dayjs(this.chat.date).format('A hh:mm')
      ) {
        return false
      }

      if (
        this.beforeChat.type === this.chat.type &&
        this.beforeChat.connectionId === this.chat.connectionId
      ) {
        return true
      }

      return false
    },
    extension() {
      let ext = ''
      const file = this.chat.file
      if (file && file.length > 0) {
        ext = file[0].fileName.split('.').pop()
      } else {
        return ''
      }

      if (ext === 'avi' || ext === 'mp4') {
        ext = 'video'
      }

      ext = ext.toLowerCase()

      return ext
    },
    subClass() {
      if (this.chat.type === 'system') {
        switch (this.chat.status) {
          case 'create':
            return 'init'
          case 'invite':
          case 'leave':
            return 'people'
          case 'stream-stop':
            return 'cancel'
          case 'stream-start':
          case 'stream-background':
            return 'alarm'
          case 'drawing':
            return 'board'
          case 'ar-deny':
          case 'ar-unsupport':
          case 'ar-pointing':
          case 'ar-area':
            return 'ar'
        }
      }
      return ''
    },
    chatText() {
      if (this.chat.type === 'system') {
        switch (this.chat.status) {
          case 'create':
            return this.$t('service.chat_create')
          case 'invite':
            return this.$t('service.chat_invite', {
              name: this.chat.name,
            })
          case 'leave':
            return this.$t('service.chat_leave', {
              name: this.chat.name,
            })
          case 'stream-stop':
            return this.$t('service.chat_stream_stop', {
              name: this.chat.name,
            })
          case 'stream-start':
            return this.$t('service.chat_stream_start', {
              name: this.chat.name,
            })
          case 'stream-background':
            return this.$t('service.chat_stream_background', {
              name: this.chat.name,
            })
          case 'drawing':
            return this.$t('service.chat_drawing', { name: this.chat.name })
          case 'ar-deny':
            return this.$t('service.chat_ar_deny')
          case 'ar-unsupport':
            return this.$t('service.chat_ar_unsupport')
          case 'ar-pointing':
            return this.$t('service.chat_ar_pointing')
          case 'ar-area':
            return this.$t('service.chat_ar_area')
        }
      }
      let chatText = this.chat.text ? this.chat.text : ''
      chatText = linkifyHtml(chatText, {
        defaultProtocol: 'https',
        className: 'chat-url',
      })
      return chatText ? chatText.replace(/\n/gi, '<br>') : ''
    },
    fileSize() {
      let size = this.chat.file[0].fileSize
      const mb = 1048576

      if (size >= mb) {
        size = size / 1024 / 1024
        return `${size.toFixed(1)}MB`
      } else {
        size = size / 1024
        return `${size.toFixed(1)}KB`
      }
    },
  },
  watch: {},
  methods: {
    download() {
      const file = this.chat.file[0]
      FileSaver.saveAs(file.fileUrl, file.fileName)
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>
