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
      <button class="chat-input__form-upload" @click="clickUpload">
        {{ $t('service.file_upload') }}
      </button>
      <input
        type="file"
        name="file"
        ref="inputFile"
        style="display: none"
        class="el-input__form-input"
        accept="*/*"
        @change="fileUpload($event)"
      />
      <textarea
        class="chat-input__form-write"
        v-model="inputText"
        :placeholder="$t('service.chat_input')"
        @keydown.enter.exact="doSend($event)"
      />

      <button class="chat-input__form-button" @click="doSend()">
        {{ $t('button.send') }}
      </button>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { uploadFile } from 'api/workspace/call'
import toastMixin from 'mixins/toast'
export default {
  name: 'ChatInput',
  mixins: [toastMixin],
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
    ...mapGetters(['chatList', 'roomInfo']),
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
    async doSend(e) {
      if (e) {
        e.preventDefault()
      }

      if (this.fileList.length > 0) {
        for (const file of this.fileList) {
          const params = {
            file: file.filedata,
            sessionId: this.roomInfo.sessionId,
            workspaceId: this.workspace.uuid,
            userId: this.account.uuid,
          }
          const res = await uploadFile(params)

          this.$call.sendFile({
            fileName: res.name,
            mimeType: file.filedata.type,
            size: res.size,
            fileDownloadUrl: res.path,
          })
        }

        this.clearUploadFile()
        this.fileList = []
      } else if (this.inputText.length > 0) {
        this.$call.sendChat(this.inputText)
      }

      this.inputText = ''
    },
    clickUpload() {
      this.unsupport()
      return
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

        const isValid = [
          'image/jpeg',
          'image/png',
          // 'image/bmp',
          // 'application/pdf',
        ].includes(file.type)

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
          this.inputText = file.name

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
          this.inputText = file.name
          // this.toastDefault(this.$t('service.file_type_notsupport'))
          // this.clearUploadFile()
          // return false
        }
      }
    },
    clearUploadFile() {
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
      this.unsupport()
      return
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
