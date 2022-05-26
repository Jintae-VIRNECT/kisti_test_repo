<template>
  <el-col class="workspace-app-setting">
    <el-card>
      <div slot="header">
        <h3>{{ $t('workspace.appSetting.title') }}</h3>
      </div>
      <div>
        <el-row>
          <el-col :span="17">
            <h4>{{ $t('workspace.appSetting.remote.title') }}</h4>
            <p>
              {{ $t('workspace.appSetting.remote.desc') }}
            </p>
          </el-col>
          <el-col :span="7">
            <el-button @click="showLogoModal = true">
              {{ $t('common.update') }}
            </el-button>
          </el-col>
        </el-row>
      </div>
    </el-card>
    <WorkspaceSettingAppLogoModal :visible.sync="showLogoModal" />
  </el-col>
</template>

<script>
export default {
  data() {
    return {
      showLogoModal: false,
    }
  },
}
</script>

<style lang="scss">
.workspace-app-setting {
  .el-card {
    color: $font-color-desc;
    font-size: 13px;
  }
  h4 {
    margin-bottom: 6px;
    color: #445168;
    font-size: 14px;
  }
  .el-row {
    display: flex;
    align-items: center;
    &:last-child {
      margin-bottom: 10px;
    }

    p {
      font-size: 12px;
    }
    .el-button {
      float: right;
    }
  }
}
</style>
