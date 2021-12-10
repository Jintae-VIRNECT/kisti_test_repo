<template>
  <el-dialog
    class="progress-modal onpremise-setting-modal"
    :visible.sync="showMe"
    :title="$t('workspace.onpremiseSetting.upload.modal.progressTitle')"
    width="380px"
    top="11vh"
  >
    <div>
      <p
        v-html="$t('workspace.onpremiseSetting.upload.modal.progressMessage')"
      ></p>
    </div>
    <div slot="footer">
      <el-button type="danger" @click="cancel">{{
        $t('workspace.onpremiseSetting.upload.modal.cancel')
      }}</el-button>
    </div>
  </el-dialog>
</template>

<script>
import modalMixin from '@/mixins/modal'
export default {
  mixins: [modalMixin],
  methods: {
    cancel() {
      this.$emit('cancel')
    },
  },
}
</script>

<style lang="scss">
#__nuxt {
  .progress-modal {
    .el-dialog__header {
      border-bottom: none;
    }
    .el-dialog__footer {
      border-top: none;
    }
  }
}
</style>
