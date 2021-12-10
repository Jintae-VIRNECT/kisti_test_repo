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
            @isSelected="isSelected"
            :selected="selected"
          />
        </el-col>
        <!-- 가운데 -->
        <el-col class="container__center">
          <ul>
            <li v-for="(file, idx) of fileList" :key="idx">
              <el-card class="onpremise-download-file">
                <OnpremiseDownloadFile
                  @fileUploadClick="fileUploadClick"
                  :file="file"
                />
              </el-card>
            </li>
          </ul>
        </el-col>
      </el-row>
    </div>
    <OnpremiseDownloadUploadModal
      :file="installFileInfo"
      :visible.sync="showFileUploadModal"
      @refresh="selectUploadPart(selected)"
    />
  </div>
</template>

<script>
import workspaceService from '@/services/workspace'
export default {
  async asyncData({ error }) {
    try {
      const downloadFiles = await workspaceService.getDownloadFiles('remote')
      return {
        fileList: downloadFiles,
      }
    } catch (e) {
      error({ message: e.message })
    }
  },
  data() {
    return {
      installFileInfo: {},
      showFileUploadModal: false,
      selected: 'remote',
    }
  },
  methods: {
    isSelected(key) {
      this.selected = key
    },
    fileUploadClick(fileInfo) {
      this.installFileInfo = fileInfo
      this.showFileUploadModal = true
    },
    async selectUploadPart(name) {
      try {
        this.fileList = await workspaceService.getDownloadFiles(name)
      } catch (e) {
        throw new Error(e.message, e.code)
      }
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
