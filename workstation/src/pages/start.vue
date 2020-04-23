<template>
  <div id="start" class="container">
    <h3 v-html="$t('start.title', { nickname })" />
    <el-form
      class="virnect-login-form"
      ref="form"
      :model="form"
      @submit.native.prevent="submit"
    >
      <el-form-item class="horizon" :label="$t('start.workspaceName')">
        <el-input
          v-model="form.name"
          :placeholder="$t('start.workspaceNamePlaceholder', { nickname })"
        />
        <span>{{ $t('start.workspaceNameComment') }}</span>
      </el-form-item>
      <el-form-item class="horizon" :label="$t('start.workspaceDesc')">
        <el-input
          v-model="form.description"
          :placeholder="$t('start.workspaceDescPlaceholder')"
        />
        <span>{{ $t('start.workspaceDescComment') }}</span>
      </el-form-item>
      <el-button>{{ $t('start.submit') }}</el-button>
    </el-form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      nickname: `User's Nickname`,
      form: {
        name: '',
        description: '',
      },
    }
  },
}
</script>

<style lang="scss">
#start {
  max-width: 380px;
  margin: 0 auto;
  padding-top: 72px;

  h3 {
    font-size: 28px;
    line-height: 37px;
    text-align: center;
  }
  .el-form {
    margin: 64px 0;
    span {
      color: $font-color-desc;
      font-size: 12.6px;
    }
  }
  .virnect-login-form .el-form-item {
    margin-bottom: 40px;
  }
}
</style>
