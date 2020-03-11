<template lang="pug">
  .box-wrapper
    .box
        .main-title 전체 공정 진행률
        .total-process-progress
          .main-percent {{ processStatistics.totalRate }}%
          el-progress(:percentage="processStatistics.totalRate" :show-text="false")
    .box
      .main-title 진행 상태 별 공정 수 
      
      .box-wrapper
        .box
          .sub-title 시작 대기 공정
          .item-container
            .item
              el-tag.custom.gray 대기
              span.value {{ processStatistics.wait }}
        .box
          .sub-title 시작된 공정
          .item-container
            .item
              el-tag.custom.silver 미진행
              span.value {{ processStatistics.unprogressing }}
            .item
              el-tag.custom.blue 진행
              span.value {{ processStatistics.progressing }}
            .item
              el-tag.custom.orange 미흡
              span.value {{ processStatistics.incompleted }}
            .item
              el-tag.custom.green 완료
              span.value {{ processStatistics.completed }}
        .box
          .sub-title 마감된 공정 (누적)
          .item-container
            .item
              el-tag.custom.dark-gray 미완수
              span.value {{ processStatistics.failed }}
            .item
              el-tag.custom.dark-blue 완수
              span.value {{ processStatistics.success }}
            .item
              el-tag.custom.dark-red 결함
              span.value {{ processStatistics.fault }}

</template>
<script>
import { mapGetters } from 'vuex'
export default {
  props: {
    data: Object,
  },
  data() {
    return {
      topic: 'table',
      done: [], // 마감된 공정
      start: [], // 시작된 공정
      idle: [], //시작 대기 공정
    }
  },
  computed: {
    ...mapGetters(['processStatistics']),
  },
}
</script>
