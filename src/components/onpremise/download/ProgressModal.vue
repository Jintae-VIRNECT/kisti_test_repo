<template>
  <el-dialog
    class="progress-modal onpremise-setting-modal"
    :visible.sync="showMe"
    title="파일 업로드 중"
    width="380px"
    top="11vh"
  >
    <div>
      <p>
        선택하신 설치 파일을 업로드하는 중입니다. 업로드가 완료될 때까지
        브라우저를 종료하지 마세요.
      </p>
    </div>
    <div slot="footer">
      <el-button type="danger" @click="cancel"> 취소 </el-button>
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
