<template lang="pug">
  .card.process-detail-graph-tooltip
    .card__header--secondary
      .card__header--left
        span.sub-title 세부공정 정보
      .card__header--right
        .text-right
          router-link.more-link(type="text" to="/process") 더보기
    .card__body.tooltip__body
      .item
        label 세부공정 이름
        p.value {{sceneGroupName}}
      .item.box-wrapper
        .box
          label 진행상태
          br
          button.btn.btn--status(:class='status') {{status | statusFilterName}}
        .box
          label 진행률
          p.progress-percent {{progress}}%
      .item
        label 세부공정 일정
        p.value {{startAt}} - {{endAt}}
      .item
        label 작업 이슈
        div
          .blub(:class="issue ? 'on' : 'off'")
          span {{issue ? "있음" : "없음"}}
</template>
<style lang="scss">
#process-detail-banner-graph .bb-tooltip-container {
  display: block !important;
  //right: 10%;
  //left: auto !important;
  margin-left: 50px;
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
  width: 300px;
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
<script>
import filters from '@/mixins/filters'
export default {
  mixins: [filters],
  props: {
    sceneGroupName: String,
    startAt: String,
    endAt: String,
    issue: Boolean,
    progress: Number,
    status: String,
  },
}
</script>
