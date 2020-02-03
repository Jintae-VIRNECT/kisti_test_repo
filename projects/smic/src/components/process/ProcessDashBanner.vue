<template lang="pug">
  .card
    .process-dash-banner
      a.toggle-topic-btn(href="#" @click.prevent="toggleTopic")
        img(src="~@/assets/image/ic-graph.svg")
        span {{topic === 'table' ? '그래프' : '테이블'}}
      process-dash-banner-table(v-if="topic === 'table'" :key="topic")
      process-dash-banner-graph(v-else)
</template>
<style lang="scss" scoped>
.process-dash-banner {
  padding: 24px 30px;
  .toggle-topic-btn {
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
    toggleTopic() {
      this.topic = this.topic === 'table' ? 'graph' : 'table'
    },
  },
  // watch: {
  //   data() {
  //     this.done
  //   },
  // },
}
</script>
