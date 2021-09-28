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
import { getFile, downloadByURL } from 'utils/file'
import toastMixin from 'mixins/toast'
import confirmMixin from 'mixins/confirm'

export default {
  name: 'ChatFileDownload',
  mixins: [toastMixin, confirmMixin],
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

      if (this.fileList.length === 1) {
        try {
          const res = await downloadFile({
            objectName: this.fileList[0].objectName,
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
            if (err.code) {
              this.toastError(this.$t('confirm.network_error'))
            }
          }
        }
      } else {
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
            if (err.code) {
              this.toastError(this.$t('confirm.network_error'))
            }
          }
        }
        this.zip.generateAsync({ type: 'blob' }).then(content => {
          FileSaver.saveAs(content, 'vremote_files.zip')
        })
      }

      this.$emit('clear')
    },
    async get(name, url) {
      const data = await getFile(url)
      this.zip.file(name, data, { binary: true })
    },
  },
}
</script>
