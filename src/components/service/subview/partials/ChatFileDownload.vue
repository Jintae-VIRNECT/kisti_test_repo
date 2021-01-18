<template>
  <div class="chat-down">
    <div class="chat-down__bar">
      <button class="chat-down__bar--cancel" @click="$emit('clear')"></button>
      <label v-if="fileList.length > 0" class="chat-down__bar--count">{{
        $t('service.file_choice_num', { number: fileList.length })
      }}</label>
      <button
        class="btn chat-down__bar--button"
        :disabled="fileList.length === 0"
        @click="download"
      >
        {{ $t('button.download') }}
      </button>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { downloadFile } from 'api/http/file'
import JSZip from 'jszip'
import FileSaver from 'file-saver'
import { getFile } from 'utils/file'
import toastMixin from 'mixins/toast'

export default {
  name: 'ChatFileDownload',
  mixins: [toastMixin],
  data() {
    return {
      chunk: [],
      zip: null,
    }
  },
  props: {
    fileList: {
      type: Array,
      default: () => {
        return []
      },
    },
  },
  computed: {
    ...mapGetters(['roomInfo']),
  },
  methods: {
    async download() {
      if (this.fileList.length === 0) return
      this.zip = new JSZip()
      for (let file of this.fileList) {
        try {
          const res = await downloadFile({
            objectName: file.objectName,
            sessionId: this.roomInfo.sessionId,
            workspaceId: this.workspace.uuid,
            userId: this.account.uuid,
          })
          await this.get(res.name, res.url)
        } catch (err) {
          this.toastError(this.$t('confirm.network_error'))
        }
      }
      this.zip.generateAsync({ type: 'blob' }).then(content => {
        FileSaver.saveAs(content, 'vremote_files.zip')
      })

      this.$emit('clear')
    },
    async get(name, url) {
      const data = await getFile(url)
      this.zip.file(name, data, { binary: true })
    },
  },
}
</script>
