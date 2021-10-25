<template>
  <el-dialog
    class="security-enhance-modal"
    :title="$t('security.enhanceModal.title')"
    :visible.sync="visible"
    width="420px"
    :before-close="handleClose"
    :close-on-click-modal="false"
  >
    <div>
      <p>{{ $t('security.enhanceModal.desc') }}</p>
      <!-- <span>{{ $t('security.enhanceModal.caution') }}</span> -->
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="changePassword">
        {{ $t('security.enhanceModal.changePassword') }}
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
