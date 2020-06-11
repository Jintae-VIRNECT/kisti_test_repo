<template>
  <div class="share-body">
    <vue2-scrollbar>
      <ol class="upload-list">
        <history-image
          v-for="img of historyList"
          :key="img.id"
          :imgInfo="img"
        ></history-image>
      </ol>
    </vue2-scrollbar>
    <button class="share-save btn small"><span>저장하기</span></button>
  </div>
</template>

<script>
import HistoryImage from './ShareHistoryImage'
import { mapGetters } from 'vuex'
export default {
  name: 'ShareHistoryList',
  components: {
    HistoryImage,
  },
  data() {
    return {
      // historyList: [],
    }
  },
  computed: {
    ...mapGetters(['historyList']),
  },
  watch: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>
