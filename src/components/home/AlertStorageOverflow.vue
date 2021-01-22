<template>
  <el-dialog
    class="storage-overflow-modal"
    :visible.sync="showMe"
    width="440px"
    top="11vh"
  >
    <div>
      <h4 v-html="$t('home.storageOverflow.title')" />
      <p v-html="$t('home.storageOverflow.desc')" />

      <el-button type="primary" @click="$router.push('/contents')">
        {{ $t('home.storageOverflow.submit') }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import modalMixin from '@/mixins/modal'

export default {
  mixins: [modalMixin],
}
</script>

<style lang="scss">
#__nuxt .storage-overflow-modal {
  .el-dialog__header {
    border-bottom: none;
  }
  .el-dialog__body {
    padding: 6px 40px 40px;
    text-align: center;

    h4 {
      margin-bottom: 8px;
      font-weight: 600;
      font-size: 16px;
      line-height: 1.5;
    }
    p {
      font-size: 13px;
    }
    .el-button {
      width: 100%;
      height: 40px;
      margin: 36px 0 8px;
    }
  }
}
</style>
