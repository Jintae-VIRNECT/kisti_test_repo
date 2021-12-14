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
              file.format.toUpperCase(),
            )
          }}
        </p>
        <button @click.prevent="$refs[file.format][0].click()">
          {{ $t('workspace.onpremiseSetting.upload.modal.add') }}
        </button>
      </div>
      <div class="drag-zone__container" v-show="file.fileSelected">
        <p>{{ file.fileName }}</p>
        <button @click.prevent="deleteFile(idx)">
          <img src="~assets/images/icon/ic-delete.svg" alt="파일삭제" />
        </button>
      </div>

      <input
        type="file"
        @change="e => changedFile(e, idx, file.format)"
        :name="file.format"
        :ref="file.format"
        :accept="`.${file.format}`"
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
    extensionList: {
      type: Array,
      required: true,
    },
  },
  methods: {
    setList() {
      this.fileList = this.extensionList.map(format => {
        return {
          fileSelected: false,
          format,
          fileName: '',
          file: null,
          dragged: false,
        }
      })
    },
    fileSet(file, idx, format) {
      const dropFormat = file.name.slice(file.name.lastIndexOf('.') + 1)

      if (dropFormat.toLowerCase() !== format.toLowerCase()) {
        this.$message.error({
          message: this.$tc(
            'workspace.onpremiseSetting.upload.error.wrongExtension',
            format,
          ),
          duration: 2000,
          showClose: true,
        })
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
        this.errorMessage('Error: 3000')
        return
      }

      this.fileSet(files[0], idx, format)
    },
    changedFile(event, idx, format) {
      this.fileSet(event.target.files[0], idx, format)
    },
    deleteFile(idx) {
      this.fileList[idx].fileName = ''
      this.fileList[idx].fileSelected = false
      this.fileList[idx].file = null
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
