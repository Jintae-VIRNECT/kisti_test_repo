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
            <!-- <workspace-storage-info />
            <workspace-download-info /> -->
          </el-row>
        </el-col>
        <!-- 가운데 -->
        <el-col class="container__center">
          <el-card class="main-banner">
            <h6>{{ $t('home.banner.sub') }}</h6>
            <h5>{{ $t('home.banner.main') }}</h5>
            <p>{{ $t('home.banner.desc') }}</p>
          </el-card>
          <current-member-list />
          <current-contents-list />
          <current-result-list />
        </el-col>
        <!-- 오른쪽 -->
        <el-col class="container__right">
          <user-profile-card />
          <!-- <link-list-card
            type="link"
            icon=""
            :title="$t('home.install.title')"
            :links="install"
          />
          <link-list-card
            type="download"
            icon=""
            :title="$t('home.guide.title')"
            :links="guide"
          /> -->
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import WorkspaceInfo from '@/components/workspace/WorkspaceInfo'
import WorkspaceStorageInfo from '@/components/home/WorkspaceStorageInfo'
import WorkspaceDownloadInfo from '@/components/home/WorkspaceDownloadInfo'
import CurrentMemberList from '@/components/home/CurrentMemberList'
import CurrentContentsList from '@/components/home/CurrentContentsList'
import CurrentResultList from '@/components/home/CurrentResultList'
import UserProfileCard from '@/components/home/UserProfileCard'
import LinkListCard from '@/components/home/LinkListCard'

import { install, guide } from '@/models/home'

export default {
  components: {
    WorkspaceInfo,
    WorkspaceStorageInfo,
    WorkspaceDownloadInfo,
    CurrentMemberList,
    CurrentContentsList,
    CurrentResultList,
    UserProfileCard,
    LinkListCard,
  },
  data() {
    return {
      install,
      guide,
    }
  },
}
</script>

<style lang="scss">
#home .container {
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
  .el-card--table .el-table__body-wrapper {
    min-height: 256px;
  }
}
</style>
