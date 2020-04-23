<template>
  <div id="start" class="container">
    <h3 v-html="$t('workspace.setting.title', { nickname })" />
    <el-form
      class="virnect-login-form"
      ref="form"
      :model="form"
      @submit.native.prevent="submit"
    >
      <el-form-item class="horizon" :label="$t('workspace.setting.name')">
        <el-input
          v-model="form.name"
          :placeholder="$t('workspace.setting.namePlaceholder', { nickname })"
        />
        <span>{{ $t('workspace.setting.nameComment') }}</span>
      </el-form-item>
      <el-form-item class="horizon" :label="$t('workspace.setting.desc')">
        <el-input
          v-model="form.description"
          :placeholder="$t('workspace.setting.descPlaceholder')"
        />
        <span>{{ $t('workspace.setting.descComment') }}</span>
      </el-form-item>
      <el-button>{{ $t('workspace.setting.start') }}</el-button>
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
