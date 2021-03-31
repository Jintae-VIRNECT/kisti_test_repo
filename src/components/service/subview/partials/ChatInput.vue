<template>
  <div class="chat-input">
    <!-- <div class="chat-input__preview" v-if="fileList.length > 0">
      <vue2-scrollbar ref="chatUploadScrollbar" :reverseAxios="true">
        <div class="chat-input__preview-list">
          <div
            v-for="(file, idx) of fileList"
            :key="'file_' + idx"
            class="chat-input__preview-file"
          >
            <img class="chat-input__preview-image" :src="file.imageUrl" />
            <button class="chat-input__preview-remove" @click="removeFile(idx)">
              {{ $t('service.file_remove') }}
            </button>
          </div>
        </div>
      </vue2-scrollbar>
    </div> -->

    <div
      class="chat-input__form"
      @dragenter.stop.prevent="dragenterHandler"
      @dragleave.stop.prevent="dragleaveHandler"
      @dragover.stop.prevent="dragoverHandler"
      @drop.stop.prevent="dropHandler"
    >
      <input
        type="file"
        name="chatfile"
        ref="inputFile"
        style="display: none"
        class="el-input__form-input"
        accept=".*"
        @change="fileUpload($event)"
      />
      <template v-if="fileList.length === 0">
        <button
          v-if="translate.flag"
          class="chat-input__form-speech"
          @click="doStt"
        >
          Speech To Text
        </button>
        <button
          v-if="useStorage"
          class="chat-input__form-upload"
          @click="clickUpload"
        >
          {{ $t('service.file_upload') }}
        </button>
        <textarea
          class="chat-input__form-write"
          v-model="inputText"
          @keyup="checkLength"
          :placeholder="$t('service.chat_input')"
          @keydown.enter.exact="doSend($event)"
        />
      </template>
      <template v-else>
        <button
          class="chat-input__form-remove"
          @click="clearUploadFile"
        ></button>
        <p class="chat-input__form-filetext">
          {{ fileName }}
        </p>
      </template>

      <!-- <button
        class="chat-input__form-button"
        style="right: 6rem;"
        @click="doTranslate"
        v-if="translate.flag"
      >
        {{ '번역' }}
      </button> -->

      <button class="chat-input__form-button" @click="doSend()">
        {{ $t('button.send') }}
      </button>
    </div>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import { uploadFile } from 'api/http/file'
import toastMixin from 'mixins/toast'
export default {
  name: 'ChatInput',
  mixins: [toastMixin],
  data() {
    return {
      viewTrans: false,
      fileList: [],
      inputText: '',
      fileName: '',
      clicked: false,
    }
  },
  props: {
    chat: Object,
  },
  computed: {
    ...mapGetters([
      'chatList',
      'roomInfo',
      'mic',
      'translate',
      'useTranslate',
      'useStorage',
    ]),
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
    // 'inputText.length': 'checkLength', // safari issue
  },
  methods: {
    ...mapActions(['useStt']),
    checkLength() {
      if (!this.useTranslate) return
      if (this.inputText.length > 200) {
        this.inputText = this.inputText.substr(0, 200)
        this.toastDefault(this.$t('service.chat_text_exceed'), {
          position: this.isTablet ? 'bottom-center' : 'top-center',
        })
      }
    },
    doStt() {
      if (!this.mic.isOn) {
        this.toastDefault(this.$t('service.stt_mic_off'))
        return
      }
      this.useStt(true)
    },
    doTranslate() {
      // if (!this.mic.isOn) {
      //   this.toastDefault('마이크가 활성화 되어있지 않습니다.')
      //   return
      // }
      this.viewTrans = !this.viewTrans
    },
    async doSend(e) {
      if (this.clicked) return
      this.clicked = true
      if (e) {
        e.preventDefault()
        if (e.keyCode === 229) return
      }

      if (this.fileList.length > 0) {
        try {
          for (const file of this.fileList) {
            const params = {
              file: file.filedata,
              sessionId: this.roomInfo.sessionId,
              workspaceId: this.workspace.uuid,
              userId: this.account.uuid,
            }
            const res = await uploadFile(params)

            this.$call.sendFile({
              fileInfo: { ...res },
            })
          }

          this.clearUploadFile()
          this.fileList = []
        } catch (err) {
          if (err && err.code) {
            if (err.code === 7002) {
              this.toastError(this.$t('service.file_dummy_assumed'))
            } else if (err.code === 7003) {
              this.toastError(this.$t('service.file_extension_unsupport'))
            } else if (err.code === 7004) {
              this.toastError(this.$t('service.file_size_exceeded'))
            }

            this.clearUploadFile()
            this.fileList = []
          } else {
            console.error(err)
          }
        }
      } else if (this.inputText.length > 0) {
        this.checkLength()
        this.$call.sendChat(this.inputText, this.translate.code)
      }

      this.inputText = ''
      this.clicked = false
    },
    clickUpload() {
      if (!this.useStorage) {
        return
      }
      if (this.fileList.length > 0) {
        // @TODO: MESSAGE
        this.toastDefault(this.$t('service.file_upload_maxnum'))
        return
      }
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
        const sizeMB = file.size / 1024 / 1024
        if (sizeMB > 20) {
          this.toastDefault(this.$t('service.file_upload_maxsize'))
          this.clearUploadFile()
          return false
        }
        // const nameExp = file.name.split('.')
        // if (
        //   !ALLOW_MINE_TYPE.includes(file.type) &&
        //   !ALLOW_EXTENSION.includes(nameExp[nameExp.length - 1])
        // ) {
        //   this.toastDefault(this.$t('service.file_type_notsupport'))
        //   this.clearUploadFile()
        //   return
        // }

        const isValid = [
          'image/jpeg',
          'image/png',
          // 'image/bmp',
          // 'application/pdf',
        ].includes(file.type)
        // const fileType = checkFileType(file)

        if (isValid) {
          const docItem = {
            id: Date.now(),
            filedata: '',
            pages: new Array(1),
            loaded: 1,
            imageUrl: null,
          }

          docItem.filedata = file
          docItem.loaded = 0
          this.fileName = file.name
          // this.inputText = file.name

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
              // @TODO: MESSAGE
              this.toastDefault(this.$t('service.file_image_notsupport'))
            }
            oImg.src = docItem.imageUrl
          }
          oReader.readAsDataURL(file)
        } else {
          const docItem = {
            id: Date.now(),
            filedata: '',
            pages: new Array(1),
            loaded: 1,
            imageUrl: null,
          }

          docItem.filedata = file
          docItem.loaded = 0
          docItem.fileData = file
          this.fileList = []
          this.fileList.push(docItem)
          this.fileName = file.name
          // this.toastDefault(this.$t('service.file_type_notsupport'))
          // this.clearUploadFile()
          // return false
        }
      }
    },
    clearUploadFile() {
      this.fileList = []
      this.$refs['inputFile'].value = ''
    },
    removeFile(idx) {
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
      if (!this.useStorage) return
      const file = event.dataTransfer.files[0]
      if (this.fileList.length > 0) {
        // @TODO: MESSAGE
        this.toastDefault(this.$t('service.file_upload_maxnum'))
        return
      } else {
        this.loadFile(file)
      }
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>
