<template>
  <li class="chat-item" :class="[chat.type, { 'file-share': isFile }]">
    <profile
      class="chat-item__profile"
      v-if="!hideProfile"
      :image="chat.profile"
      :thumbStyle="{ width: '2.143rem', height: '2.143rem' }"
    ></profile>
    <div class="chat-item__body" :class="[chat.type, { hidden: hideProfile }]">
      <div class="chat-item__body--chatbox">
        <span class="chat-item__body--name" v-if="!hideProfile">
          {{ chat.name }}
        </span>
        <div v-if="isFile" class="chat-item__file">
          <div class="chat-item__file--wrapper">
            <div class="chat-item__file--name" :class="fileType">
              {{ fileInfo.name }}
            </div>
          </div>
          <span class="chat-item__file--exp">
            {{ fileInfo.exp }}
          </span>
          <span class="chat-item__file--size">{{ fileSize }}</span>
        </div>
        <div class="chat-item__body--textbox">
          <p
            v-if="isTranslate"
            class="chat-item__body--text"
            :class="{
              inactive: !translateActive && !translate.multiple,
              multiple: isTranslate && translate.multiple,
            }"
            v-html="chatTranslateText"
          ></p>
          <p
            class="chat-item__body--text"
            :class="[
              subClass,
              {
                inactive:
                  isTranslate && !translate.multiple ? translateActive : false,
                multiple: isTranslate && translate.multiple,
              },
            ]"
            v-html="chatText"
          ></p>
        </div>
        <button
          v-if="isTranslate && !translate.multiple"
          class="chat-item__translate--button"
          :class="{ active: translateActive }"
          @click="translateActive = !translateActive"
        >
          {{ $t('service.translate') }}
        </button>
        <button
          v-if="isFile && chat.type === 'opponent'"
          class="chat-item__file--button"
          @click="download"
        >
          <span class="button-text">{{ $t('button.download') }}</span>
        </button>
      </div>
      <span v-if="!hideTime" class="chat-item__body--time">
        {{ $dayjs(chat.date).format('A hh:mm') }}
      </span>
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
import { downloadByURL } from 'utils/file'
import { checkFileType } from 'utils/fileTypes'
import toastMixin from 'mixins/toast'
import confirmMixin from 'mixins/confirm'
export default {
  name: 'ChatItem',
  mixins: [toastMixin, confirmMixin],
  components: {
    Profile,
  },
  data() {
    return {
      translateText: '',
      translateActive: false,
    }
  },
  props: {
    beforeChat: Object,
    afterChat: Object,
    chat: Object,
  },
  computed: {
    ...mapGetters(['roomInfo', 'translate']),
    isTranslate() {
      if (
        this.translateText.length > 0 &&
        this.translateText !== this.chat.text
      ) {
        return true
      } else {
        return false
      }
    },
    translateCode() {
      if (this.translate && this.translate.flag && this.translate.code) {
        return this.translate.code.split('-')[0]
      } else {
        return false
      }
    },
    isFile() {
      return !!this.chat.file
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
    fileType() {
      const file = this.chat.file
      if (file) {
        return checkFileType({
          name: file.name,
          type: file.contentType,
        })
      } else {
        return ''
      }
    },
    fileInfo() {
      const file = this.chat.file
      if (file) {
        const idx = file.name.lastIndexOf('.')
        return {
          exp: file.name.toLowerCase().substr(idx + 1),
          name: file.name.substr(0, idx),
        }
      } else {
        return {
          exp: '',
          name: '',
        }
      }
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
      chatText = linkifyHtml(chatText.replace(/\</g, '&lt;'), {
        defaultProtocol: 'https',
        className: 'chat-url',
      })
      return chatText ? chatText.replace(/\n/gi, '<br>') : ''
    },
    chatTranslateText() {
      if (this.chat.type === 'system') {
        return systemText(this.chat.status, this.chat.name)
      }
      let chatText = this.translateText ? this.translateText : ''
      if (typeof chatText === 'object') {
        return ''
      }
      chatText = linkifyHtml(chatText.replace(/\</g, '&lt;'), {
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
  methods: {
    ...mapActions(['updateChat']),
    async download() {
      try {
        const res = await downloadFile({
          objectName: this.chat.file.objectName,
          sessionId: this.roomInfo.sessionId,
          workspaceId: this.workspace.uuid,
          userId: this.account.uuid,
        })
        //타블렛 사파리가 파일을 현재 페이지에서 열어버리는 동작을 막기 위함.
        if (this.isTablet && this.isSafari) {
          const usingNewTab = true
          downloadByURL(res, usingNewTab)
        } else {
          downloadByURL(res)
        }
      } catch (err) {
        if (err === 'popup_blocked') {
          this.confirmDefault(this.$t('confirm.please_allow_popup'))
        } else {
          this.toastError(this.$t('confirm.network_error'))
        }
      }
    },
    async doTranslateText() {
      try {
        if (this.translateCode === false) return
        const response = await doTranslate(this.chat.text, this.translateCode)
        this.translateText = response
        this.translateActive = true
        if (!this.chat.mute) {
          this.$emit('tts', response)
        }
      } catch (err) {
        console.error(`${err.message} (${err.code})`)
      }
    },
  },

  /* Lifecycles */
  mounted() {
    if (this.chat.type === 'opponent' && !this.isFile) {
      if (this.translate.flag) {
        this.doTranslateText()
      } else {
        this.$emit('tts', this.chat.text)
      }
    }
  },
}
</script>
