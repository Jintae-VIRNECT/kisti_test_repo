<template>
  <el-col class="workspace-onpremise-setting">
    <el-card>
      <div slot="header">
        <h3>{{ $t('workspace.onpremiseSetting.title') }}</h3>
      </div>
      <div>
        <p>{{ $t('workspace.onpremiseSetting.desc') }}</p>
        <el-row>
          <el-col :span="17">
            <h4>{{ $t('workspace.onpremiseSetting.company.title') }}</h4>
            <p>{{ $t('workspace.onpremiseSetting.company.desc') }}</p>
          </el-col>
          <el-col :span="7">
            <el-button @click="showCompanyNameModal = true">
              {{ $t('common.update') }}
            </el-button>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="17">
            <h4>{{ $t('workspace.onpremiseSetting.logo.title') }}</h4>
            <p>{{ $t('workspace.onpremiseSetting.logo.desc') }}</p>
          </el-col>
          <el-col :span="7">
            <el-button @click="showLogoModal = true">
              {{ $t('common.update') }}
            </el-button>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="17">
            <h4>{{ $t('workspace.onpremiseSetting.favicon.title') }}</h4>
            <p>{{ $t('workspace.onpremiseSetting.favicon.desc') }}</p>
          </el-col>
          <el-col :span="7">
            <el-button @click="showFaviconModal = true">
              {{ $t('common.update') }}
            </el-button>
          </el-col>
        </el-row>
      </div>
    </el-card>

    <OnpremiseWorkspaceCompanyNameModal :visible.sync="showCompanyNameModal" />
    <OnpremiseWorkspaceLogoModal :visible.sync="showLogoModal" />
    <OnpremiseWorkspaceFaviconModal :visible.sync="showFaviconModal" />
  </el-col>
</template>

<script>
export default {
  data() {
    return {
      showCompanyNameModal: false,
      showLogoModal: false,
      showFaviconModal: false,
    }
  },
}
</script>

<style lang="scss">
.workspace-onpremise-setting {
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
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid #ebeef5;
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
