<template>
  <div class="share-body__file pdf">
    <button class="share-body__backbutton" @click="back">
      <tooltip :content="file.name" placement="bottom-start" width="auto">
        <p class="share-body__backbutton-text" slot="body">
          {{ file.name }}
        </p>
      </tooltip>
    </button>
    <vue2-scrollbar>
      <ol class="upload-list inner">
        <sharing-pdf-image
          v-for="(sharing, idx) of sharingList"
          :key="'sharing' + idx"
          :fileInfo="file"
          :pageInfo="sharing"
          :pdfPage="sharing.pageNum"
        ></sharing-pdf-image>
      </ol>
    </vue2-scrollbar>
  </div>
</template>

<script>
import Tooltip from 'Tooltip'
import SharingPdfImage from './SharingPdfImage'
import { mapGetters } from 'vuex'
export default {
  name: 'SharePdfView',
  components: {
    Tooltip,
    SharingPdfImage,
  },
  props: {
    file: {
      type: Object,
      default: () => {
        return {}
      },
    },
  },
  data() {
    return {}
  },
  computed: {
    ...mapGetters(['pdfPages']),
    sharingList() {
      if (this.file === null) {
        return []
      } else {
        return this.pdfPages[this.file.id]
      }
    },
  },
  methods: {
    back() {
      this.$emit('back')
    },
  },
}
</script>
