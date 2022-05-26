<template>
  <el-dialog
    class="progress-modal onpremise-setting-modal"
    :visible.sync="showMe"
    :title="$t('workspace.onpremiseSetting.upload.modal.progressTitle')"
    :close-on-press-escape="false"
    :close-on-click-modal="false"
    :show-close="false"
    width="380px"
    top="11vh"
  >
    <div>
      <p
        v-html="$t('workspace.onpremiseSetting.upload.modal.progressMessage')"
      ></p>
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
    .el-dialog__title {
      &:after {
        position: absolute;
        content: '...';
        width: 6px;
        overflow: hidden;
        white-space: nowrap;
        animation: loadingDot 1.6s linear infinite;
      }
    }
  }
}
@keyframes loadingDot {
  0% {
    width: 0;
  }
  100% {
    width: 16px;
  }
}
</style>
