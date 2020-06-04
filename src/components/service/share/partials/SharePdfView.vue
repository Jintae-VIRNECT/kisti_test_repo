<template>
  <div class="share-body">
    <vue2-scrollbar>
      <ol class="upload-list">
        <li>
          <button class="share-body__backbutton">
            <p>{{ `설계 도면.pdf` }}</p>
          </button>
        </li>
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
export default {
  name: 'SharePdfView',
  components: {
    SharingImage,
  },
  data() {
    return {
      sharingList: [],
    }
  },
  computed: {},
  watch: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>
