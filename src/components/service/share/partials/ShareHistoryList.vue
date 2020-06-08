<template>
  <div class="share-body">
    <vue2-scrollbar>
      <ol class="upload-list">
        <sharing-image
          v-for="(file, idx) of historyList"
          :key="'sharing' + idx"
          :fileInfo="file"
          :isPdf="file.pdf"
        ></sharing-image>
      </ol>
    </vue2-scrollbar>
    <button class="share-save btn small"><span>저장하기</span></button>
  </div>
</template>

<script>
import SharingImage from './SharingImage'
export default {
  name: 'ShareHistoryList',
  components: {
    SharingImage,
  },
  data() {
    return {
      historyList: [],
    }
  },
  computed: {},
  watch: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>
