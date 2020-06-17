<template>
  <div class="chat-input">
    <div class="chat-input__preview" v-if="fileList.length > 0">
      <vue2-scrollbar ref="chatUploadScrollbar" :reverseAxios="true">
        <div class="chat-input__preview-list">
          <div
            v-for="(file, idx) of fileList"
            :key="'file_' + idx"
            class="chat-input__preview-file"
          >
            <img class="chat-input__preview-image" :src="file.imageUrl" />
            <button class="chat-input__preview-remove" @click="removeFile(idx)">
              파일 삭제
            </button>
          </div>
        </div>
      </vue2-scrollbar>
    </div>

    <div
      class="chat-input__form"
      @dragenter.stop.prevent="dragenterHandler"
      @dragleave.stop.prevent="dragleaveHandler"
      @dragover.stop.prevent="dragoverHandler"
      @drop.stop.prevent="dropHandler"
    >
      <button class="chat-input__form-upload" @click="clickUpload">
        파일 업로드
      </button>
      <input
        type="file"
        name="file"
        ref="inputFile"
        style="display: none"
        class="el-input__form-input"
        accept="image/jpeg,image/png"
        @change="fileUpload($event)"
      />
      <textarea
        class="chat-input__form-write"
        v-model="inputText"
        placeholder="메시지를 입력하세요."
        @keydown.shift.enter.exact="newLine()"
        @keydown.enter.exact="doSend($event)"
      />

      <button class="chat-input__form-button" @click="doSend()">보내기</button>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
  name: 'ChatInput',
  components: {},
  data() {
    return {
      fileList: [],
      inputText: '',
    }
  },
  props: {
    chat: Object,
  },
  computed: {
    ...mapGetters(['chatList']),
  },
  watch: {
    fileList: {
      handler() {
        this.$nextTick(() => {
          if (this.$refs['chatUploadScrollbar']) {
            this.$refs['chatUploadScrollbar'].scrollToY(Number.MAX_SAFE_INTEGER)
          }
        })
      },
      deep: true,
    },
  },
  methods: {
    /**
     * 채팅 object 추가
     * @param {String} chatType : 'system' | 'me' | 'opponent'
     * @param {Object | String} msg
     *        'system' : { type: 'start'|'file'|'document'|'camera'|'ar', name: filename|username }
     *        'me'|'opponent' : text message
     */
    addChatItem(chatType, msg) {
      // const datetime = this.$moment()
      const datetime = this.$dayjs()
      // 작성시간 노출 계산
      if (this.chatList.length > 0) {
        const pastChat = this.chatList[0]
        if (pastChat) {
          if (pastChat.chatType === chatType) {
            if (
              this.$dayjs(pastChat.datetime).format('HHmm') ===
              datetime.format('HHmm')
            ) {
              this.$set(pastChat, 'datetime', false)
            }
          }
        }
      }

      this.chatList.push({
        text: msg,
        date: new Date(),
        type: 'me',
      })
      // this.$nextTick(() => {
      //   if (this.$refs['chatScrollbar']) {
      //     this.$refs['chatScrollbar'].scrollToY(Number.MAX_SAFE_INTEGER)
      //   }
      // })
    },

    doSend(e) {
      console.log(e)
      if (e) {
        e.preventDefault()
      }
      this.$call.sendChat(this.inputText)

      this.inputText = ''
    },
    clickUpload() {
      this.$refs['inputFile'].click()
    },
    fileUpload(e) {
      const files = e.target.files
      if (files.length > 0) {
        this.loadFile(files[0])
      }
    },
    loadFile(file) {
      if (file) {
        if (file.size / 1024 / 1024 > 20) {
          alert('파일 사이즈가 너무 커요.')
          this.clearUploadFile()
          return false
        }

        if (
          ['image/jpeg', 'image/png', 'image/bmp', 'application/pdf'].includes(
            file.type,
          )
        ) {
          const docItem = {
            id: Date.now(),
            filedata: '',
            pages: new Array(1),
            loaded: 1,
            imageUrl: null,
          }

          docItem.filedata = file
          docItem.loaded = 0

          const oReader = new FileReader()
          oReader.onload = event => {
            docItem.imageUrl = event.target.result
            const oImg = new Image()

            oImg.onload = event => {
              this.fileList.push(docItem)
              event.target.remove()
            }
            oImg.onerror = () => {
              //이미지 아닐 시 처리.
              alert('This image is unavailable.')
            }
            oImg.src = docItem.imageUrl
          }
          oReader.readAsDataURL(file)
        } else {
          alert('파일형식이 맞지않음')
          this.clearUploadFile()
          return false
        }
      }
    },
    clearUploadFile() {
      this.$refs['inputFile'].value = ''
    },
    removeFile(idx) {
      // console.log(file)
      this.fileList.splice(idx, 1)
    },

    dragenterHandler(event) {
      // console.log(event);
    },
    dragleaveHandler(event) {
      // console.log(event);
    },
    dragoverHandler(event) {
      // console.log(event);
    },
    dropHandler(event) {
      const file = event.dataTransfer.files[0]
      this.loadFile(file)
    },
  },

  /* Lifecycles */
  mounted() {
    //console.log(this.$dayjs)
    // const message = `${this.opponent.name || '알수없는 사용자'}님과 통화를 시작합니다.`
    // const message = this.$t('service.chat_call_start', { name: `${this.opponent.name || this.$t('service.chat_anonymous_user')}` })
    // this.addChatItem('system', '알수없는 누군가와 통신하겠소')
  },
  beforeDestroy() {
    //this.$remoteSDK.removeMessageListener(this.receiveMessage)
    this.$eventBus.$off('addChatItem', this.addChatItem)
  },
}
</script>
