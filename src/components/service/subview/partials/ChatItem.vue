<template>
  <li
    class="chat-item"
    :class="[type, { 'file-share': chat.file && chat.file.length > 0 }]"
  >
    <profile class="profile" v-if="!hideProfile"></profile>
    <div class="chat-item__body" :class="{ hidden: hideProfile }">
      <div class="chatbox">
        <span class="name">{{ chat.name }}</span>
        <div v-if="chat.file && chat.file.length > 0" class="chat-item__file">
          <div class="chat-item__file--wrapper">
            <div class="chat-item__file--icon" :class="getClass"></div>
            <div class="chat-item__file--name">
              {{ chat.file[0].fileName }}
            </div>
          </div>
          <p class="chat-item__file--size">{{ fileSize }}</p>
        </div>
        <p
          v-if="chat.text !== undefined"
          class="text"
          :class="getClass"
          v-html="chatText"
        ></p>
        <button
          v-if="chat.file && chat.file.length > 0"
          class="chat-item__file--button"
          @click="download"
        >
          <span class="button-text">다운로드</span>
        </button>
      </div>
      <span v-if="!hideTime" class="time">{{
        $dayjs(chat.date).format('A hh:mm')
      }}</span>
    </div>
  </li>
</template>

<script>
import Profile from 'Profile'
import FileSaver from 'file-saver'
import { TYPE, SUB_TYPE } from 'configs/chat.config'
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
    type() {
      if (false === this.chat.type || this.chat.type === TYPE.OPPONENT) {
        return 'opponent'
      } else if (this.chat.type === TYPE.ME) {
        return 'me'
      } else {
        return 'system'
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

      if (this.afterChat.uuid !== this.chat.uuid) {
        return false
      }

      if (this.chat.uuid === null) {
        return false
      }

      return true
    },
    hideProfile() {
      if (this.beforeChat === null) {
        return false
      }

      if (
        this.beforeChat.type === this.chat.type &&
        this.beforeChat.uuid === this.chat.uuid
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
      }

      if (ext === 'avi' || ext === 'mp4') {
        ext = 'video'
      }

      ext = ext.toLowerCase()

      return ext
    },
    getClass() {
      return {
        txt: this.extension === 'txt',
        png: this.extension === 'png',
        pdf: this.extension === 'pdf',
        mp3: this.extension === 'mp3',
        jpg: this.extension === 'jpg',
        video: this.extension === 'video',
        ar: this.type === 'system' && this.chat.subType === SUB_TYPE.AR,
        alarm: this.type === 'system' && this.chat.subType === SUB_TYPE.ALARM,
        people: this.type === 'system' && this.chat.subType === SUB_TYPE.PEOPLE,
        cancel: this.type === 'system' && this.chat.subType === SUB_TYPE.CANCEL,
        board: this.type === 'system' && this.chat.subType === SUB_TYPE.BOARD,
      }
    },
    chatText() {
      return this.chat.text.replace(/\n/gi, '<br>')
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
