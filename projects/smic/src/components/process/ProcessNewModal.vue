<template lang="pug">
  .process-new-modal
    el-dialog(
      :visible.sync="processModal"
      width="540px"
      height="50vh"
      @open="handleOpen"
      @close="handleCancel"
      :destroy-on-close="true")
      template(slot="title")
        span.process-new-modal__header-title 신규 공정 등록
      .process-new-modal__body(v-if="target")
        .section.border-divider
          label 등록될 공정 이름
          .value
            span {{target.contentName}}
        .section.border-divider
          label 세부공정 목록
          .value
            .number-label {{target.contentSize}}
        .detail-process-list.border-divider
          .detail-process-item
            .section.title
              label 01.
              .value
                span Scene Group's name
            .section.title
              label 02.
              .value
                span Scene Group's name
      span.dialog-footer.section(slot='footer')
        el-button(type='primary' @click='handleConfirm') 다음
</template>
<script>
export default {
  props: {
    toggleProcessModal: Boolean,
    target: {
      type: Object,
    },
  },
  data() {
    return {
      processModal: false,
    }
  },
  methods: {
    handleCancel() {
      this.processModal = false
      this.$emit('onToggleNewModal', false)
    },
    handleOpen() {},
    handleConfirm() {
      this.$emit('handleConfirm')
    },
  },
  watch: {
    $props: {
      handler() {
        this.processModal = this.$props.toggleProcessModal
      },
      deep: true,
    },
  },
}
</script>
