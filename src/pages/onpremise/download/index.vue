<template>
  <div id="workspace-download">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>{{
            $t('menu.workspaceSetting')
          }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{
            $t('menu.collapse.settings.download')
          }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('menu.collapse.settings.download') }}</h2>
      </div>
      <el-row>
        <!-- 왼쪽 -->
        <el-col class="container__left">
          <OnpremiseDownloadInstallFileUpload
            @selectUploadPart="selectUploadPart"
          />
        </el-col>
        <!-- 가운데 -->
        <el-col class="container__center">
          <ul>
            <li>
              <OnpremiseDownloadFile @fileUploadClick="fileUploadClick" />
            </li>
          </ul>
        </el-col>
      </el-row>
    </div>
    <OnpremiseDownloadUploadModal
      :fileName="installFileName"
      :visible.sync="showFileUploadModal"
    />
  </div>
</template>

<script>
export default {
  data() {
    return {
      installFileName: '',
      showFileUploadModal: false,
    }
  },
  methods: {
    fileUploadClick(name) {
      this.installFileName = name
      this.showFileUploadModal = true
    },
    selectUploadPart(name) {
      // 넘어오는 name으로 출력될 리스트 처리
      console.log(name)
    },
  },
}
</script>

<style lang="scss">
#workspace-download .container__center.el-col-24 {
  width: 1364px;
  margin-right: 0;
  .el-card__body {
    padding: 0;
  }
  .left.el-col-24 {
    width: 240px;
    padding: 28px 30px;
  }
  .right.el-col-24 {
    width: 568px;
    padding: 28px 40px;
  }
}
</style>
