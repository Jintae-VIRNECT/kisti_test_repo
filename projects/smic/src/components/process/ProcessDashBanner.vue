<template lang="pug">
  .card
    .process-dash-banner
      .vn-label.toggle-topic-btn
        a(v-show="topic === 'graph'" href="#" @click.prevent="toggleGraphTable") 
          img(src="~@/assets/image/ic-graph.svg")
          span 그래프
        a(v-show="topic === 'table' " href="#" @click.prevent="toggleGraphTable") 
          img(src="~@/assets/image/ic-table.svg")
          span 테이블
      process-dash-banner-table(v-if="topic === 'table'" :key="topic")
      process-dash-banner-graph(v-else)
</template>
<style lang="scss" scoped>
.process-dash-banner {
  padding: 24px 30px;
  .toggle-topic-btn {
    position: relative;
    z-index: 1;
    float: right;
  }
}
</style>
<script>
import ProcessDashBannerTable from '@/components/process/ProcessDashBannerTable.vue'
import ProcessDashBannerGraph from '@/components/process/ProcessDashBannerGraph.vue'

export default {
  components: {
    ProcessDashBannerTable,
    ProcessDashBannerGraph,
  },
  props: {
    data: Array,
  },
  data() {
    return {
      topic: 'table',
    }
  },
  methods: {
    toggleGraphTable() {
      this.topic = this.topic === 'table' ? 'graph' : 'table'
    },
  },
}
</script>
