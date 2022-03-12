<template>
  <ul>
    <li
      class="drag-zone"
      :class="{ 'drag-zone--dragged': fileList[idx].dragged }"
      @dragenter="e => dragenter(e, idx)"
      @dragover="e => dragover(e, idx)"
      @dragleave="e => dragleave(e, idx)"
      @drop="e => drop(e, idx, file.format)"
      v-for="(file, idx) of fileList"
      :key="idx"
    >
      <div class="drag-zone__container" v-show="!file.fileSelected">
        <p class="drag-info">
          +
          {{
            $tc(
              'workspace.onpremiseSetting.upload.modal.drag',
              file.format.toString().toUpperCase(),
            )
          }}
        </p>
        <button @click.prevent="$refs[file.format][0].click()">
          {{ $t('workspace.onpremiseSetting.upload.modal.add') }}
        </button>
      </div>
      <div class="drag-zone__container" v-show="file.fileSelected">
        <p>{{ file.fileName }}</p>
        <button @click.prevent="deleteFile(idx, $refs[file.format][0])">
          <img src="~assets/images/icon/ic-delete.svg" alt="파일삭제" />
        </button>
      </div>

      <input
        type="file"
        @change="e => changedFile(e, idx, file.format)"
        :name="file.format"
        :ref="file.format"
        :accept="getFileAccept(file.format)"
      />
    </li>
  </ul>
</template>

<script>
import messageMixin from '@/mixins/message'

const setSplit = str => {
  if (str.length > 30)
    return str.slice(0, 30) + '…' + str.slice(str.lastIndexOf('.'))
  else return str
}
export default {
  mixins: [messageMixin],
  data() {
    return {
      fileList: [],
    }
  },
  props: {
    file: {
      type: Object,
      required: true,
    },
    extensionList: {
      type: Array,
      required: true,
    },
  },
  methods: {
    getFileAccept(formats) {
      if (typeof formats === 'string') {
        return `.${formats.toLowerCase()}`
      } else {
        return formats
          .map(format => {
            return `.${format.toLowerCase()}`
          })
          .toString()
      }
    },
    setList() {
      if (this.file.category === 'iOS') {
        this.fileList = this.extensionList.map(format => {
          return {
            fileSelected: false,
            format,
            fileName: '',
            file: null,
            dragged: false,
          }
        })
      } else {
        this.fileList = [
          {
            fileSelected: false,
            format: [...this.extensionList],
            fileName: '',
            file: null,
            dragged: false,
          },
        ]
      }
    },
    checkFileName(name) {
      /**
       * 기본 정규식 :  /^(remote|make|view)[_](mobile|hololens2|linkflow|realwear|pc)[_]([0-9]{7})(.apk|.exe|.appx|.appxbundle)$/
       */
      const product = this.file.productName.toLowerCase()
      const deviceType = this.file.category.toLowerCase()
      const extensions =
        this.file.extensionList.length === 2
          ? `.${this.file.extensionList[0]}|.${this.file.extensionList[1]}`
          : `.${this.file.extensionList[0]}`

      const nameRegExp = new RegExp(
        `^(${product}[_](${deviceType})[_])([0-9]{7})(${extensions})$`,
      )
      if (nameRegExp.test(name)) return true
      else return false
    },
    checkFileVersion(name) {
      const patterns = name.split('_')
      const fileInfo = patterns[2]
      const versionCode = fileInfo.split('.')[0]
      const uploadedFileVersion = this.file.versionCode
      return versionCode > uploadedFileVersion
    },
    initFile(format) {
      this.$refs[format][0].value = ''
    },
    fileSet(file, idx, format) {
      if (this.checkFileName(file.name) === false) {
        this.$message.error({
          message: this.$t('workspace.onpremiseSetting.upload.error.name'),
          duration: 2000,
          showClose: true,
        })
        this.initFile(format)
        this.$emit('fileTypeError')
        return false
      }

      if (this.checkFileVersion(file.name) === false) {
        this.$message.error({
          message: this.$t('workspace.onpremiseSetting.upload.error.version'),
          duration: 2000,
          showClose: true,
        })
        this.initFile(format)
        this.$emit('fileTypeError')
        return false
      }

      const dropFormat = file.name.slice(file.name.lastIndexOf('.') + 1)

      if (
        !format
          .toLocaleString()
          .toLowerCase()
          .includes(dropFormat.toLowerCase())
      ) {
        this.$message.error({
          message: this.$tc(
            'workspace.onpremiseSetting.upload.error.wrongExtension',
            format,
          ),
          duration: 2000,
          showClose: true,
        })
        this.initFile(format)
        this.$emit('fileTypeError')
        return false
      }

      if (file.size === 0) {
        this.$message.error({
          message: this.$t('workspace.onpremiseSetting.upload.error.size'),
          duration: 2000,
          showClose: true,
        })
        this.initFile(format)
        this.$emit('fileTypeError')
        return false
      }

      this.fileList[idx].fileName = setSplit(file.name)
      this.fileList[idx].fileSelected = true
      this.fileList[idx].file = file

      this.$emit('fileData', this.fileList)
    },
    drop(event, idx, format) {
      event.preventDefault()
      const files = event.dataTransfer.files

      if (files.length !== 1) {
        this.$message.error({
          message: this.$t('workspace.onpremiseSetting.upload.error.oneFile'),
          duration: 2000,
          showClose: true,
        })
        this.initFile(format)
        return false
      }

      this.fileSet(files[0], idx, format)
    },
    changedFile(event, idx, format) {
      this.fileSet(event.target.files[0], idx, format)
    },
    deleteFile(idx, refs) {
      this.fileList[idx].fileName = ''
      this.fileList[idx].fileSelected = false
      this.fileList[idx].file = null
      refs.value = null
      this.$emit('fileData', this.fileList)
    },
    dragenter(event, idx) {
      this.fileList[idx].dragged = true
    },
    dragover(event, idx) {
      event.preventDefault()
      this.fileList[idx].dragged = true
    },
    dragleave(event, idx) {
      this.fileList[idx].dragged = false
    },
  },
  mounted() {
    this.setList()
  },
}
</script>

<style lang="scss">
.is-error {
  .drag-zone {
    padding: 23px 20px;
    border: solid 2px #f64f4e;
    border-radius: 3px;
  }
}
.drag-zone {
  margin-bottom: 8px;
  padding: 27px 24px;
  background-color: rgba(244, 246, 250, 0.8);
  border: 1.5px dashed #e0e5ef;
  &__container {
    display: flex;
    justify-content: space-between;
  }
  .drag-info {
    width: 274px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }
  &--dragged {
    padding: 24px 21px;
    border: 3px dashed #e0e5ef;
  }
  button {
    @include fontLevel(100);
    color: #0052cc;
    vertical-align: middle;
  }
  input[type='file'] {
    display: none;
  }
}
#__nuxt {
  .el-dialog__body .drag-info {
    color: #5e6b81;
    @include fontLevel(75);
  }
}
</style>
