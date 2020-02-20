<template lang="pug">
  .process-new-modal.issue-modal.report-modal
    el-dialog(
      :visible.sync="reportModal"
      width="540px"
      height="50vh"
      @open="handleOpen"
      @close="handleCancel"
      :destroy-on-close="true")
      template(slot="title")
        span.process-new-modal__header-title 리포트
      .process-new-modal__body
        .section.border-divider
          label 항목 1.
          .value
            span 질문 내용입니까?
            span.bool 아니요
        .section.border-divider
          label 항목 2.
          .value
            span 질문 내용입니까?
            span.bool 네
        .section.border-divider
          label 항목 3.
          .value
            span 질문 내용입니까?
            p 결함 정보 확인 필요로 합니다.
        .section
          label 항목 4.
          .value
            span 질문내용입니까?질문내용입니까?질문내용입니까?질문내용입니까?질문내용입니까?
              el-button 
                img(src="~@/assets/image/ic-download.svg")
                span 다운로드
        .section
          el-image(:src="imgSrc" :preview-src-list="[imgSrc]")
          i.el-icon-full-screen
</template>

<style lang="scss">
.report-modal .el-dialog .section {
  &.border-divider {
    margin: 18px 30px;
    padding: 0 0 18px;
    &:first-child {
      margin-top: 26px;
    }
  }
  label {
    min-width: 60px;
  }
  .value {
    width: 390px;
  }
  .bool {
    display: block;
    width: 63px;
    margin-top: 8px;
    padding: 2px;
    text-align: center;
    border: solid 1.2px #566173;
    border-radius: 16px;
    opacity: 0.94;
  }
  p {
    margin-bottom: 0;
    color: #566173;
  }
  & > .el-icon-full-screen {
    right: 0;
  }
}
</style>

<script>
export default {
  props: {
    toggleReportModal: Boolean,
    target: {
      type: Object,
    },
  },
  data() {
    return {
      reportModal: false,
      imgSrc: require('@/assets/image/issue-sample.jpg'),
    }
  },
  methods: {
    handleCancel() {
      this.reportModal = false
      this.$emit('handleCancel')
    },
    handleOpen() {},
    handleConfirm() {},
  },
  watch: {
    $props: {
      handler() {
        this.reportModal = this.$props.toggleReportModal
      },
      deep: true,
    },
  },
}
</script>
