<template>
  <li
    class="chat-item"
    :class="[type, { 'file-share': chat.file && chat.file.length > 0 }]"
  >
    <profile class="profile" v-if="!hideProfile"></profile>
    <!-- <img
      class="profile"
      src="~assets/image/call/chat_img_user.svg"
      v-if="!hideProfile"
    /> -->
    <div class="chat-item__body" :class="{ hidden: hideProfile }">
      <div class="chatbox">
        <span class="name">{{ chat.name }}</span>
        <div v-if="chat.file && chat.file.length > 0" class="chat-item__file">
          <div class="chat-item__file--wrapper">
            <div class="chat-item__file--icon" :class="getClass"></div>
            <div class="chat-item__file--name">
              {{ chat.file[0].filename }}
            </div>
          </div>
          <p class="chat-item__file--size">{{ chat.file[0].filesize }}</p>
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
        >
          <span class="button-text">다운로드</span>
        </button>
      </div>
      <span v-if="!hideTime" class="time">{{
        $dayjs(chat.date).format('A hh:mm')
      }}</span>
      <!-- <span v-if="!hideTime" class="time">{{ chat.date }}</span> -->
    </div>
  </li>
</template>

<script>
import Profile from 'Profile'
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
      if (false === this.chat.type || this.chat.type === 'opponent') {
        return 'opponent'
      } else if (this.chat.type === 'me') {
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

      if (this.chat.uuid === null && this.chat.name === 'alarm') {
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
      if (this.chat.file && this.chat.file.length > 0) {
        ext = this.chat.file[0].filename.split('.').pop()
      }

      if (ext === 'avi' || ext === 'mp4') {
        ext = 'video'
      }

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
        alarm: this.type === 'system' && this.chat.subType === 'alarm',
        people: this.type === 'system' && this.chat.subType === 'people',
        cancel: this.type === 'system' && this.chat.subType === 'cancel',
        ar: this.type === 'system' && this.chat.subType === 'ar',
        board: this.type === 'system' && this.chat.subType === 'board',
      }
    },
    chatText() {
      return this.chat.text.replace(/\n/gi, '<br>')
    },
  },
  watch: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>
