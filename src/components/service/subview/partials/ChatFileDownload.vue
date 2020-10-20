<template>
  <div class="chat-down">
    <div class="chat-down__bar">
      <button class="chat-down__bar--cancel" @click="$emit('clear')"></button>
      <label v-if="fileList.length > 0" class="chat-down__bar--count">{{
        $t('service.file_choice_num', { number: fileList.length })
      }}</label>
      <button class="chat-down__bar--button" @click="download">
        {{ $t('button.download') }}
      </button>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { downloadFileUrl } from 'api/http/file'
import JSZip from 'jszip'
import FileSaver from 'file-saver'

const getFile = url => {
  return new Promise(resolve => {
    var xhr = new XMLHttpRequest()

    xhr.addEventListener('load', function() {
      if (xhr.status == 200) {
        resolve(xhr.response)
      }
    })

    xhr.open('GET', url)
    xhr.responseType = 'blob'
    xhr.send(null)
  })
}
export default {
  name: 'ChatFileDownload',
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
      this.zip = new JSZip()
      for (let file of this.fileList) {
        const res = await downloadFileUrl({
          objectName: file.objectName,
          sessionId: this.roomInfo.sessionId,
          workspaceId: this.workspace.uuid,
          userId: this.account.uuid,
        })
        await this.get(res.name, res.url)
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
