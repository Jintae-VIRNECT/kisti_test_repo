<template lang="pug">
  .process-new-modal.smart-tool-modal
    el-dialog(
      :visible.sync="smartToolModal"
      width="540px"
      height="50vh"
      @open="handleOpen"
      @close="handleCancel"
      :destroy-on-close="true")
      template(slot="title")
        span.process-new-modal__header-title 스마트툴
      .process-new-modal__body
        .section
          label Job ID
          .value
            span Job ID no.
            el-divider(direction="vertical")
            span 체결 완료 수
            span.blue 4
            span /10
        .section
          label 체결 1.
          .value
            span 100%
            el-progress(:percentage="100" :show-text="false")
        .section
          label 체결 2.
          .value
            span 0%
            el-progress(:percentage="0" :show-text="false")
      
</template>

<style lang="scss">
.smart-tool-modal .el-dialog .section {
  padding: 24px 30px;

  .el-divider {
    margin: 0 28px;
  }
  label {
    min-width: 70px;
  }
  .value {
    & > span {
      vertical-align: middle;
      &:first-child {
        color: #0d2a58;
        font-weight: 500;
      }
      &.blue {
        margin: 0 4px 0 16px;
        color: #186ae2;
      }
    }
    .el-progress {
      margin-top: 12px;
    }
  }
}
</style>

<script>
export default {
  props: {
    toggleSmartToolModal: Boolean,
    target: {
      type: Object,
    },
  },
  data() {
    return {
      smartToolModal: false,
      imgSrc: require('@/assets/image/issue-sample.jpg'),
    }
  },
  methods: {
    handleCancel() {
      this.smartToolModal = false
      this.$emit('handleCancel')
    },
    handleOpen() {},
    handleConfirm() {},
  },
  watch: {
    $props: {
      handler() {
        this.smartToolModal = this.$props.toggleSmartToolModal
      },
      deep: true,
    },
  },
}
</script>
