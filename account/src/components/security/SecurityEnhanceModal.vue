<template>
  <el-dialog
    class="security-enhance-modal"
    title="기기 보완 강화"
    :visible.sync="visible"
    width="420px"
    :before-close="handleClose"
  >
    <div>
      <p>
        알지 못하는 기기가 있다면 다른 사람이 나의 계정을 도용하여 사용하였을
        수도 있습니다. 비밀번호를 변경하여 계정을 보호하세요.
      </p>
      <span>현재 사용 중인 기기를 제외한 모든 기기에서 로그아웃 됩니다.</span>
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button type="confirm" @click="changePassword">
        비밀번호 변경
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import dialogMixin from '@/mixins/dialog'

export default {
  mixins: [dialogMixin],
  props: {
    data: Object,
  },
  methods: {
    changePassword() {
      this.$router.push('/profile')
    },
  },
}
</script>

<style lang="scss">
.security-enhance-modal .el-dialog__body {
  span {
    display: inline-block;
    margin: 6px 0;
    color: $font-color-desc;
    font-size: 13px;
  }
}
</style>
