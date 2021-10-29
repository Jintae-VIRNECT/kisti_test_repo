<template>
  <div class="share-body__file pdf">
    <button class="share-body__backbutton" @click="back">
      <img
        :src="
          isMobileSize
            ? require('assets/image/call/icon_back_new.svg')
            : require('assets/image/call/ic_back.svg')
        "
        alt="back"
      />
      <p v-if="isMobileSize" class="share-body__backbutton-text" slot="body">
        {{ file.name }}
      </p>
      <tooltip
        v-else
        :content="file.name"
        placement="bottom-start"
        width="-webkit-fill-available"
      >
        <p class="share-body__backbutton-text" slot="body">
          {{ file.name }}
        </p>
      </tooltip>
    </button>
    <vue2-scrollbar ref="pdf-list-scroll">
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
      this.$refs['pdf-list-scroll'].scrollToY(0)
      this.$emit('back')
    },
  },
}
</script>
