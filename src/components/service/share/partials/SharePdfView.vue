<template>
  <div class="share-body pdf">
    <button class="share-body__backbutton" @click="back">
      <p>{{ `설계 도면.pdf` }}</p>
    </button>
    <vue2-scrollbar>
      <ol class="upload-list inner">
        <sharing-image
          v-for="(file, idx) of sharingList"
          :key="'sharing' + idx"
          :fileInfo="file"
        ></sharing-image>
      </ol>
    </vue2-scrollbar>
  </div>
</template>

<script>
import SharingImage from './SharingImage'
import { mapGetters } from 'vuex'
export default {
  name: 'SharePdfView',
  components: {
    SharingImage,
  },
  props: {
    id: {
      type: Number,
      default: 0,
    },
  },
  data() {
    return {}
  },
  computed: {
    ...mapGetters(['pdfPages']),
    sharingList() {
      if (this.id === 0) {
        return []
      } else {
        return this.pdfPages[this.id]
      }
    },
  },
  watch: {
    id(val) {
      console.log(val)
      console.log(this.pdfPages)
    },
  },
  methods: {
    back() {
      this.$emit('back')
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>
