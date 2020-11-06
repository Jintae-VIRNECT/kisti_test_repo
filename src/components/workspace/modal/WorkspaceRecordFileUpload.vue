<template>
  <modal
    title=""
    width="31.2857rem"
    height="11.5rem"
    :showClose="true"
    :visible.sync="visible"
    :beforeClose="beforeClose"
    customClass="modal-upload"
    :class="{ modalless: true }"
    :dimClose="false"
  >
    <div class="record-file-upload">
      <p class="record-file-upload--header">파일을 업로드중입니다.</p>
      <p class="record-file-upload--current-file">{{ currentFile }}</p>
      <progress-bar
        customClass="progress-upload"
        :value="uploaded"
        :max="fileIds.length"
      ></progress-bar>
      <p class="record-file-upload--filecount">{{ filecount }}</p>
    </div>
  </modal>
</template>

<script>
import Modal from 'Modal'
import ProgressBar from 'ProgressBar'

import { uploadRecordFile } from 'api/http/file'
import IDBHelper from 'utils/idbHelper'
import confirmMixin from 'mixins/confirm'

export default {
  name: 'WorkspaceFileUpload',
  mixins: [confirmMixin],
  components: {
    Modal,
    ProgressBar,
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    fileIds: {
      type: Array,
      default: () => [],
      required: true,
    },
  },
  data() {
    return {
      currentFile: '',
      uploaded: 0,
    }
  },
  computed: {
    filecount() {
      const total = this.fileIds.length
      return `${this.uploaded} / ${total}`
    },
  },
  watch: {
    visible(current, before) {
      if (current === false && before === true) {
        this.close()
      }
    },
    fileIds() {
      this.upload()
    },
  },
  methods: {
    beforeClose() {
      this.$emit('update:visible', false)
    },
    async upload() {
      if (this.fileIds.length === 0) {
        this.close()
        return
      }

      try {
        const chunks = await IDBHelper.getMediaChunkArrays(this.fileIds)
        for (const chunk of chunks) {
          if (!this.visible) {
            return
          }
          const file = new File([chunk.blob], chunk.fileName)
          this.currentFile = chunk.fileName
          await uploadRecordFile({
            file: file,
            sessionId: chunk.sessionId,
            workspaceId: this.workspace.uuid,
            userId: this.account.uuid,
            durationSec: Math.trunc(chunk.playTime),
          })
          this.uploaded++
        }
      } catch (e) {
        this.confirmDefault('업로드 중 오류가 발생했습니다.​', {
          action: this.close,
        })
        console.error(e)
      } finally {
        this.close()
      }
    },
    close() {
      this.uploaded = 0
      this.$emit('update:visible', false)
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>

<style lang="scss">
@import '~assets/style/vars';

.modal.modal-upload {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background-color: rgba(#121517, 0.5);

  &.modalless {
    background-color: unset;
  }

  .modal--inner {
    position: fixed;
    bottom: 1.4286em;
    left: 1.4286em;
    background-color: $color_darkgray_500;
    border-radius: 4px;
  }
  .modal--header {
    height: 2em;
    padding: 0;
    border: none;
  }
  .modal--body {
    padding: 0;
  }
  .modal--close {
    top: 0.6429em;
    right: 0.6429em;
    z-index: 1;

    &:hover {
      opacity: 1;
    }
  }
}

.record-file-upload {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.record-file-upload--header {
  margin-bottom: 0.5em;
  font-weight: 500;
  font-size: 1.2857em;
  text-align: center;
}

.record-file-upload--current-file {
  margin-bottom: 0.7143em;
  color: #859dba;
  font-weight: 500;
  font-size: 1em;
  text-align: center;
}

.record-file-upload--filecount {
  margin-bottom: 0.7143em;
  font-weight: 500;
  font-size: 0.8571em;
  text-align: center;
}

.progress.progress-upload {
  position: relative;
  width: 25.7143em;
  height: 1em;
  margin-bottom: 0.7143em;
  background-color: #1e1e20;
  border: solid 1px#000000;
  border-radius: 7px;

  .progress-front {
    height: 100%;
    overflow: hidden;
    background: linear-gradient(-90deg, #5eb7ff 0%, #368fff 100%);
    border-radius: 7px;
    transition: all 0.5s;
  }
}
</style>
