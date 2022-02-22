<template>
  <el-dialog
    class="progress-modal"
    :visible.sync="showMe"
    :title="title"
    width="380px"
    top="11vh"
    append-to-body
    :close-on-click-modal="false"
    :show-close="false"
    :close-on-press-escape="false"
  >
    <div>
      <p v-html="desc"></p>
      <el-progress
        :text-inside="true"
        :stroke-width="26"
        :percentage="progress"
      ></el-progress>
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
  props: {
    progress: Number,
    title: String,
    desc: String,
    cancelTitle: {
      type: String,
      default: 'common.message.fileRequestCanceTitle',
    },
    cancelDesc: {
      type: String,
      default: 'common.message.fileRequestCancelDesc',
    },
  },
  data() {
    return {
      showMe: false,
    }
  },
  methods: {
    async cancel() {
      try {
        await this.$confirm(
          this.$t(this.cancelDesc),
          this.$t(this.cancelTitle),
          {
            confirmButtonText: this.$t('common.confirm'),
            dangerouslyUseHTMLString: true,
          },
        )
        this.$emit('cancel')
      } catch (e) {
        return false
      }
    },
  },
}
</script>

<style lang="scss">
#__nuxt {
  .progress-modal {
    .el-dialog__header {
      div > * {
        display: inline-block;
        vertical-align: middle;
      }
    }
  }
}
</style>
