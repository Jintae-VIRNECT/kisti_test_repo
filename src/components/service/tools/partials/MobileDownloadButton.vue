<template>
  <button
    class="mobile-download-btn"
    :disabled="disabled"
    @click="download"
  ></button>
</template>

<script>
import FileSaver from 'file-saver'
import { base64ToBlob } from 'utils/file'

import confirmMixin from 'mixins/confirm'
import { mapGetters } from 'vuex'

export default {
  name: 'MobileDownloadButton',
  mixins: [confirmMixin],
  props: {
    disabled: { type: Boolean },
  },
  computed: {
    ...mapGetters(['historyList', 'shareFile']),
  },
  methods: {
    download() {
      this.confirmCancel(
        this.$t('service.share_save'),
        {
          text: this.$t('button.confirm'),
          action: this.save,
        },
        {
          text: this.$t('button.cancel'),
        },
      )
    },
    async save() {
      const history = this.historyList.find(
        history => history.id === this.shareFile.id,
      )
      const dataType = 'application/octet-stream'
      const file = await base64ToBlob(history.img, dataType, history.fileName)
      FileSaver.saveAs(file)
      this.selected = []
      return
    },
  },
}
</script>

<style lang="scss" scoped>
@import '~assets/style/mixin';

.mobile-download-btn {
  @include mobile-circle-btn($new_color_bg_button_sub2);
  @include mobile-circle-btn-icon(
    '~assets/image/call/mdpi_icon_download_new.svg'
  );
  @include mobile-circle-btn-disabled(
    $new_color_bg_button_disabled,
    $new_color_bg_button_sub,
    '~assets/image/call/mdpi_icon_download_new.svg'
  );
}
</style>
