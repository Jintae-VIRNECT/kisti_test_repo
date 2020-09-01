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
import { downloadFile } from 'api/workspace/call'
import { mapGetters } from 'vuex'
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
    ...mapGetters(['roomInfo']),
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
    async download() {
      const res = await downloadFile({
        fileName: this.chat.file.fileName,
        sessionId: this.roomInfo.sessionId,
        workspaceId: this.workspace.uuid,
        userId: this.account.uuid,
      })
      console.log(res)
      // FileSaver.saveAs(file.fileUrl, file.fileName)
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>
