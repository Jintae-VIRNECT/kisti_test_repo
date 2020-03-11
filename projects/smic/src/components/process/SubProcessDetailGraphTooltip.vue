<template lang="pug">
  .card.process-detail-graph-tooltip.sub-process-detail-graph-tooltip
    .card__header--secondary
      .card__header--left
        span.sub-title 작업 정보
    .card__body.tooltip__body
      .item
        label 작업 이름
        p.value {{data.name}}
      .item.box-wrapper
        .box
          label 진행 상태
          br
          button.btn.btn--status(:class='data.status | processStatusFilterName | processStatusNameColor') {{data.status | processStatusFilterName}}
        .box
          label 진행률
          p.progress-percent {{data.progressRate}}%
      .item
        label 보고일시
        p.value {{data.reportedDate | dayJs_FilterDateTimeFormat}}
      .item.box-wrapper
        .box
          label 리포트
          p(v-if="data.report")
            el-button(data-type="report" :data-index="index") 리포트 보기
          p(v-else) ―
        .box
          label 작업 이슈
          p(v-if="data.issue")
            el-button(data-type="issue" :data-index="index") 작업 이슈 보기
          p(v-else) ―
      .item
        label 스마트툴 (Job ID / 체결정보)
        div.smartTool(v-if="data.smartTool")
          span Job ID no. {{ data.smartTool.smartToolJobId }}
          el-divider(direction="vertical")
          span.count {{ data.smartTool.smartToolWorkedCount }} 
          span &nbsp;/ {{ data.smartTool.smartToolBatchTotal }} &nbsp;
          el-button(data-type="smartTool" :data-index="index") 스마트툴 보기
        p(v-else) ―

</template>

<script>
import filters from '@/mixins/filters'
import dayjs from '@/plugins/dayjs'

export default {
  mixins: [filters, dayjs],
  props: {
    data: Object,
    index: Number,
  },
}
</script>

<style lang="scss">
.sub-process-detail-graph-tooltip {
  bottom: 0;
  height: 460px;

  .el-button {
    display: block;
    margin-top: 4px;
    padding: 8px;
    color: #0d2a58;
    font-weight: 500;
    font-size: 13px;
    background-color: #f2f5f9;
    border: none;
    border-radius: 4px;
    &:hover {
      background: #e6e9ee;
    }
    &:active {
      color: #fff;
      background: #455163;
    }
  }
  .smartTool {
    margin-top: 8px;
    & > * {
      display: inline-block;
    }
    .el-button {
      display: block;
      margin-top: 14px;
    }
  }
}
</style>
