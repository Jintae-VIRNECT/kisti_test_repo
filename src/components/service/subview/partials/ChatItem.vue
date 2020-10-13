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
            <!-- <div class="chat-item__file--icon" :class="extension"></div> -->
            <div class="chat-item__file--name" :class="extension">
              {{ chat.file.fileName }}
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
import { systemClass, systemText } from './chatUtils'
import { downloadFile } from 'api/http/file'
import { mapGetters, mapActions } from 'vuex'
import { translate as doTranslate } from 'plugins/remote/translate'
import { languageCode } from 'utils/translate'
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
    ...mapGetters(['roomInfo', 'translate']),
    translateCode() {
      if (this.translate && this.translate.flag && this.translate.code) {
        const idx = languageCode.findIndex(
          lang => lang.sttCode === this.translate.code,
        )
        if (idx < 0) {
          return false
        }
        return languageCode[idx].code
      } else {
        return false
      }
    },
    isFile() {
      if (this.chat.file) {
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
      if (file) {
        ext = file.fileName.split('.').pop()
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
        return systemClass(this.chat.status)
      }
      return ''
    },
    chatText() {
      if (this.chat.type === 'system') {
        return systemText(this.chat.status, this.chat.name)
      }
      let chatText = this.chat.text ? this.chat.text : ''
      if (typeof chatText === 'object') {
        return ''
      }
      chatText = linkifyHtml(chatText, {
        defaultProtocol: 'https',
        className: 'chat-url',
      })
      return chatText ? chatText.replace(/\n/gi, '<br>') : ''
    },
    fileSize() {
      let size = this.chat.file.size
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
    ...mapActions(['updateChat']),
    async download() {
      const res = await downloadFile({
        fileName: this.chat.file.fileName,
        sessionId: this.roomInfo.sessionId,
        workspaceId: this.workspace.uuid,
        userId: this.account.uuid,
      })
      // FileSaver.saveAs(file.fileUrl, file.fileName)
    },
    async translateText() {
      try {
        if (this.translateCode === false) return
        const response = await doTranslate(this.chat.text, this.translateCode)
        this.updateChat({
          id: this.chat.id,
          text: `${this.chat.text}<br><b>${response}</b>`,
        })
      } catch (err) {
        console.error(`${err.message} (${err.code})`)
      }
    },
  },

  /* Lifecycles */
  mounted() {
    if (
      this.chat.type === 'opponent' &&
      this.translate.flag &&
      this.chat.languageCode !== this.translate.code
    ) {
      this.translateText()
    }
  },
}
</script>
