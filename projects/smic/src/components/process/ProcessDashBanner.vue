<template lang="pug">
  .card
    .card__header
      .card__header--left
        span.title 실시간 공정 진행 상태
          span.small 접속시간 기준의 공정 진행 상태
    .process-dash-banner
      .vn-label.toggle-topic-btn
        a(v-show="topic === 'table'" href="#" @click.prevent="toggleGraphTable") 
          img(src="~@/assets/image/ic-graph.svg")
          span 그래프 보기
        a(v-show="topic === 'graph' " href="#" @click.prevent="toggleGraphTable") 
          img(src="~@/assets/image/ic-table.svg")
          span 그래프 접기
      process-dash-banner-table
      process-dash-banner-graph(v-if="topic !== 'table'" :key="topic" @changeFilter="changeFilter")
</template>
<style lang="scss">
.process-dash-banner {
  padding: 24px 30px;
  .toggle-topic-btn {
    position: relative;
    z-index: 1;
    float: right;
  }
  .bb {
    margin-top: 10px;
  }
  .bb-event-rects .bb-event-rect {
    cursor: pointer;
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
    initTopic: String,
  },
  data() {
    return {
      topic: this.initTopic,
    }
  },
  methods: {
    toggleGraphTable() {
      this.topic = this.topic === 'table' ? 'graph' : 'table'
    },
    changeFilter(data) {
      this.$emit('changeFilter', data)
    },
  },
  created() {
    this.$store.dispatch('getProcessStatistics')
  },
}
</script>
