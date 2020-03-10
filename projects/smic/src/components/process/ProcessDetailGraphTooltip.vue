<template lang="pug">
  .card.process-detail-graph-tooltip
    .card__header--secondary
      .card__header--left
        span.sub-title 세부공정 정보
      .card__header--right
        .text-right
          router-link.more-link(type="text" :to="`${ $router.currentRoute.path }/${ data.subProcessId }`") 더보기
    .card__body.tooltip__body
      .item
        label 세부공정 이름
        p.value {{data.name}}
      .item.box-wrapper
        .box
          label 진행 상태
          br
          button.btn.btn--status(:class='data.conditions | processStatusFilterName | processStatusNameColor') {{data.conditions | processStatusFilterName}}
        .box
          label 진행률
          p.progress-percent {{data.progressRate}}%
      .item
        label 세부공정 일정
        p.value {{data.startDate | dayJs_FilterDateTimeFormat}} - {{data.endDate | dayJs_FilterDateTimeFormat}}
      .item
        label 작업 이슈
        div
          .blub(:class="data.issuesTotal ? 'on' : 'off'")
          span {{data.issuesTotal ? "있음" : "없음"}}
</template>

<script>
import filters from '@/mixins/filters'
import dayjs from '@/plugins/dayjs'

export default {
  mixins: [filters, dayjs],
  props: {
    data: Object,
  },
}
</script>

<style lang="scss">
#process-detail-graph .bb-tooltip-container {
  top: 0 !important;
  right: 40px;
  bottom: 0 !important;
  left: auto !important;
  display: block !important;
  visibility: visible !important;
  pointer-events: auto !important;

  &:before {
    display: inline-block;
    height: 100%;
    vertical-align: middle;
    content: '';
  }
}
.process-detail-graph-tooltip {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 30px;
  width: 300px;
  height: 340px;
  margin: auto 0;

  .tooltip__body {
    padding: 16px;
  }
  .box-wrapper {
    display: flex;
    .box {
      flex: 1 1 auto;
    }
  }
  label {
    margin-bottom: 0px;
    color: #6d798b;
    font-size: 12px;
  }
  .item {
    margin-bottom: 20px;
    &:last-child {
      margin-bottom: 0px;
    }
  }
  p {
    margin: 0px;
  }
  p.value {
    margin: 4px 0px 0px 0px;
    color: #0d2a58;
    font-size: 14px;
  }
  .progress-percent {
    color: #0d2a58;
    font-size: 24px;
  }
  .btn.btn--status {
    margin-top: 7px;
  }
  .blub.on {
    background-color: #ee5c57;
  }
  .blub.off {
    background-color: #aabbce;
  }
  .blub {
    display: inline-block;
    width: 6px;
    height: 6px;
    margin-right: 10px;
    vertical-align: middle;
    border-radius: 50%;
  }
}
</style>
