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
        <div v-if="chat.file && chat.file.length > 0" class="file">
          <p class="file__name">
            {{ chat.file[0].filename }}
          </p>
          <p class="file__size">{{ chat.file[0].filesize }}</p>
        </div>
        <p
          v-if="chat.text !== undefined"
          class="text"
          :class="{
            alarm: type === 'system' && chat.name === 'alarm',
            people: type === 'system' && chat.name === 'people',
            cancel: type === 'system' && chat.name === 'cancel',
            ar: type === 'system' && chat.name === 'ar',
            board: type === 'system' && chat.name === 'board',
          }"
          v-html="chat.text"
        ></p>
        <button v-if="chat.file && chat.file.length > 0" class="file__button">
          <p class="button__text">다운로드</p>
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
import Profile from 'profile'
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
  },
  watch: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>
