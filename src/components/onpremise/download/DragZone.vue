<template>
  <div
    class="drag-zone"
    :class="{ 'drag-zone--dragged': dragged }"
    @dragenter="dragenter"
    @dragover="dragover"
    @dragleave="dragleave"
    @drop="drop"
  >
    <div class="drag-zone__container" v-show="!fileSelected">
      <p>+ IPA 파일을 이곳에 드래그해 주세요. 또는</p>
      <button @click.prevent="$refs[fileType].click()">파일 선택</button>
    </div>
    <div class="drag-zone__container" v-show="fileSelected">
      <p>{{ fileName }}</p>
      <button @click.prevent="deleteFile(fileType)">
        <img src="~assets/images/icon/ic-delete.svg" alt="파일삭제" />
      </button>
    </div>

    <input
      type="file"
      name=""
      :ref="fileType"
      id=""
      @change="changedFile"
      :accept="`.${fileType}`"
    />
  </div>
</template>

<script>
import messageMixin from '@/mixins/message'
export default {
  mixins: [messageMixin],
  data() {
    return {
      fileSelected: false,
      fileName: '',
      dragged: false,
    }
  },
  props: {
    fileType: {
      type: String,
      required: true,
    },
  },
  methods: {
    dragenter() {
      this.dragged = true
    },
    dragover(event) {
      event.preventDefault()
      this.dragged = true
    },
    dragleave() {
      this.dragged = false
    },
    drop(event) {
      event.preventDefault()
      this.isDragged = false
      const files = event.dataTransfer.files

      if (files.length !== 1) {
        this.errorMessage('Error: 3000')
        return false
      }

      this.fileName = files[0].name
      this.fileSelected = true
    },
    changedFile(event) {
      this.fileName = event.target.files[0].name
      this.fileSelected = true
    },
    deleteFile(fileType) {
      console.log(fileType)
      this.fileSelected = false
    },
  },
}
</script>

<style lang="scss">
.is-error {
  .drag-zone {
    border-radius: 3px;
    border: solid 2px #f64f4e;
    padding: 23px 20px;
  }
}
.drag-zone {
  width: 380px;
  height: 74px;
  background-color: rgba(244, 246, 250, 0.8);
  border: 1.5px dashed #e0e5ef;
  padding: 27px 24px;
  &__container {
    display: flex;
    justify-content: space-between;
  }
  p {
    @include fontLevel(100);
    color: #5e6b81;
    width: 274px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  &--dragged {
    border: 3px dashed #e0e5ef;
    padding: 24px 21px;
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
</style>
