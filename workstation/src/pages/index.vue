<template>
  <div id="home">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item to="/">{{ $t('menu.home') }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('menu.home') }}</h2>
      </div>
      <!-- 왼쪽 -->
      <el-row>
        <el-col class="container__left">
          <el-row>
            <el-card class="el-card--table">
              <workspace-info />
            </el-card>
          </el-row>
          <el-row>
            <workspace-storage-info />
            <workspace-download-info />
          </el-row>
        </el-col>
        <!-- 가운데 -->
        <el-col class="container__center">
          <el-card class="main-banner">
            <h6>{{ $t('main.banner.sub') }}</h6>
            <h5>{{ $t('main.banner.main') }}</h5>
            <p>{{ $t('main.banner.desc') }}</p>
          </el-card>
        </el-col>
        <!-- 오른쪽 -->
        <el-col class="container__right"> <el-card></el-card></el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import WorkspaceInfo from '@/components/workspace/WorkspaceInfo'
import WorkspaceStorageInfo from '@/components/workspace/WorkspaceStorageInfo'
import WorkspaceDownloadInfo from '@/components/workspace/WorkspaceDownloadInfo'

export default {
  components: {
    WorkspaceInfo,
    WorkspaceStorageInfo,
    WorkspaceDownloadInfo,
  },
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
#home .container {
  width: 1752px;

  .main-banner {
    height: 190px;
    padding: 10px;
    color: #fff;
    background: url('~assets/images/img-home-banner.jpg');
    background-size: cover;
    border: none;

    & h6 {
      font-weight: 300;
      font-size: 20px;
      font-family: $poppins;
    }
    & h5 {
      font-size: 28px;
      font-family: $poppins;
    }
    & p {
      margin-top: 20px;
      font-size: 20px;
      opacity: 0.9;
    }
  }
}
</style>
