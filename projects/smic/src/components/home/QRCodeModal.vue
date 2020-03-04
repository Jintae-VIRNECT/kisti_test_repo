<template lang="pug">
  .process-new-modal.issue-modal.qr-modal
    el-dialog(
      :visible.sync="qrModal"
      width="540px"
      height="50vh"
      @open="handleOpen"
      @close="handleCancel"
      :destroy-on-close="true")
      template(slot="title")
        span.process-new-modal__header-title QR 코드
      .process-new-modal__body
        .section
          el-image(:src="getUser.qrCode")
          a(:href="getUser.qrCode" download)
            el-button
              img(src="~@/assets/image/ic-download.svg")
              span 저장
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  props: {
    toggleQRModal: Boolean,
  },
  data() {
    return {
      qrModal: false,
    }
  },
  computed: {
    ...mapGetters(['getUser']),
  },
  methods: {
    handleCancel() {
      this.qrModal = false
      this.$emit('handleCancel')
    },
    handleOpen() {},
    handleConfirm() {},
  },
  watch: {
    $props: {
      handler() {
        this.qrModal = this.$props.toggleQRModal
      },
      deep: true,
    },
  },
}
</script>

<style lang="scss">
.process-new-modal.issue-modal.qr-modal {
  .section {
    padding: 20px 30px 30px;
    text-align: center;
    .el-image {
      display: block;
    }
  }
  .el-dialog .el-button {
    padding: 6px 20px;
  }
}
</style>
