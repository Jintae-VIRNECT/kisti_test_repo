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
        @keydown.enter.exact="doSend($event)"
      />

      <button class="chat-input__form-button" @click="doSend()">보내기</button>
    </div>
  </div>
</template>

<script>
import { mapGetters, mapState } from 'vuex'
import { sendFile } from 'api/workspace/call'

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
    ...mapState({
      room: state => state.room,
    }),
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
      console.log(e)
      if (e) {
        e.preventDefault()
      }
      this.$call.sendChat(this.inputText)

      if (this.fileList.length > 0) {
        for (const file of this.fileList) {
          const response = await sendFile(
            file.filedata,
            this.room.roomId,
            this.workspace.uuid,
          )
          console.log(response)
          const downUrl = response.downloadUrl

          const params = {
            fileName: file.filedata.name,
            mimeType: file.filedata.type,
            size: file.filedata.size,
            fileDownloadUrl: downUrl,
          }

          this.$call.sendFile(params)
        }

        this.clearUploadFile()
        this.fileList = []
      }

      this.inputText = ''
    },
    clickUpload() {
      if (this.fileList.length > 0) {
        console.log('현재 파일 업로드는 1개씩만 지원합니다.')
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
  mounted() {},
}
</script>
