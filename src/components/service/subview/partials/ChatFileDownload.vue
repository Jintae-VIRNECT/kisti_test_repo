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
import { downloadFile } from 'api/http/file'
import { downloadByDataURL } from 'utils/file'
export default {
  name: 'ChatFileDownload',
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
      for (let file of this.fileList) {
        const res = await downloadFile({
          filePath: file.path,
          sessionId: this.roomInfo.sessionId,
          workspaceId: this.workspace.uuid,
          userId: this.account.uuid,
        })

        downloadByDataURL(res, file.name)
      }
      this.$emit('clear')
    },
  },
}
</script>
