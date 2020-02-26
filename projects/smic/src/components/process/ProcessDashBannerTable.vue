<template lang="pug">
  .box-wrapper
    .box
        .main-title 전체 공정 진행률
        .total-process-progress
          .main-percent 88%
          el-progress(:percentage="88" :show-text="false")
    .box
      .main-title 진행 상태 별 공정 수 
      
      .box-wrapper
        .box
          .sub-title 시작 대기 공정
          .item-container
            .item
              el-tag.custom.gray 대기
              span.value 14
        .box
          .sub-title 시작된 공정
          .item-container
            .item
              el-tag.custom.silver 미진행
              span.value 3
            .item
              el-tag.custom.blue 진행
              span.value 24
            .item
              el-tag.custom.orange 미흡
              span.value 14
            .item
              el-tag.custom.green 완료
              span.value 44
        .box
          .sub-title 마감된 공정 (누적)
          .item-container
            .item
              el-tag.custom.dark-gray 미완수
              span.value 14
            .item
              el-tag.custom.dark-blue 완수
              span.value 24
            .item
              el-tag.custom.dark-red 결함
              span.value 14

</template>
<script>
export default {
  props: {
    data: Array,
  },
  data() {
    return {
      topic: 'table',
      done: [], // 마감된 공정
      start: [], // 시작된 공정
      idle: [], //시작 대기 공정
    }
  },
  // methods: {
  //   toggleTopic() {
  //     this.topic = this.topic === 'table' ? 'graph' : 'table'
  //   },
  // },
  watch: {
    data() {
      this.done
    },
  },
}
</script>
