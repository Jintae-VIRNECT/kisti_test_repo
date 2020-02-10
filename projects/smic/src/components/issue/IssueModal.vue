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
        .section
          label 이슈 유형
          .value
            span 작업 이슈
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
</template>

<style lang="scss">
.issue-modal {
  .section {
    padding: 30px 30px 0 30px;
    &.border-divider {
      padding: 30px;
    }
  }
  .el-image img {
    pointer-events: inherit;
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
