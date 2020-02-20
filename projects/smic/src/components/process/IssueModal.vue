<template lang="pug">
  .process-new-modal.issue-modal
    el-dialog(
      :visible.sync="issueModal"
      width="540px"
      height="50vh"
      @open="handleOpen"
      @close="handleCancel"
      :destroy-on-close="true")
      template(slot="title")
        span.process-new-modal__header-title 이슈 관리
      .process-new-modal__body
        .section.border-divider
          el-image(:src="imgSrc" :preview-src-list="[imgSrc]")
          i.el-icon-full-screen
        .section.issue
          label 이슈 유형
          .value
            span.issue-type 작업 이슈
        .section
          label 보고자
          .value
            span 버넥트
        .section
          label 보고일시
          .value
            span 2020.3.18  19:00
        .section
          label 이슈내용
          .value
            span 0000(년)_00(월)_00(일)_00(시)_00(분)_id.jpg
              el-button 
                img(src="~@/assets/image/ic-download.svg")
                span 다운로드
        .section
          label 공정 이름
          .value
            span Content’s name
        .section
          label 세부공정 이름
          .value
            span Scene Group's name
        .section
          label 작업 이름
          .value
            span Content’s name
              el-button 
                img(src="~@/assets/image/ic-shortcut.svg")
                span 작업 바로가기
</template>

<style lang="scss">
.issue-modal .el-dialog {
  .section {
    position: relative;
    margin: 20px 30px;
    padding: 0;

    &.border-divider {
      margin: 0;
      padding: 30px;
    }
    &.issue {
      margin: 30px;
    }
    &:last-child {
      margin-bottom: 36px;
    }
    & > .el-icon-full-screen {
      position: absolute;
      right: 30px;
      margin: 12px;
      padding: 6px;
      color: #fff;
      font-size: 19px;
      background: rgba(0, 0, 0, 0.5);
      border-radius: 50%;
      pointer-events: none;
    }
    .issue-type {
      padding: 7px 8px;
    }
  }
  .el-image img {
    pointer-events: inherit;
  }
  i.el-icon-circle-close {
    color: #fff;
  }
  .el-button {
    display: block;
    margin-top: 12px;
    padding: 2px 10px;
    color: #0d2a58;
    font-weight: 500;
    background-color: #f2f5f9;
    border: none;
    border-radius: 4px;
    box-shadow: none;
    &:hover {
      background: #e6e9ee;
    }
    &:active {
      color: #fff;
      background: #455163;
      span > img {
        filter: brightness(20);
      }
    }
    span > img {
      display: inline-block;
      margin: 0 4px 0 -2px;
      vertical-align: middle;
    }
    span > span {
      display: inline-block;
      vertical-align: middle;
    }
  }
}
</style>

<script>
export default {
  props: {
    toggleIssueModal: Boolean,
    target: {
      type: Object,
    },
  },
  data() {
    return {
      issueModal: false,
      imgSrc: require('@/assets/image/issue-sample.jpg'),
    }
  },
  methods: {
    handleCancel() {
      this.issueModal = false
      this.$emit('handleCancel')
    },
    handleOpen() {},
    handleConfirm() {},
  },
  watch: {
    $props: {
      handler() {
        this.issueModal = this.$props.toggleIssueModal
      },
      deep: true,
    },
  },
}
</script>
