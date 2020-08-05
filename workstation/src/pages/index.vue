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
          <el-row v-if="activeWorkspace.role === 'MASTER'">
            <plans-used
              i18nGroup="home.plansInfo.arStorage"
              :info="plansInfo.storage"
            />
            <plans-used
              i18nGroup="home.plansInfo.arContent"
              :info="plansInfo.viewCount"
            />
            <plans-used
              i18nGroup="home.plansInfo.call"
              :info="plansInfo.callTime"
            />
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
          <link-list-card
            :icon="require('assets/images/icon/ic-phonelink.svg')"
            :title="$t('home.install.title')"
            :links="install"
          />
          <link-list-card
            :icon="require('assets/images/icon/ic-local-library.svg')"
            :title="$t('guide.title')"
            :links="guide"
          />
          <a :href="`${$url.www}/support/faq`" target="_blank">
            <el-card class="faq-banner">
              <h6>
                <span>{{ $t('home.faq.title') }}</span>
                <i class="el-icon-right" />
              </h6>
              <p v-html="$t('home.faq.desc')" />
            </el-card>
          </a>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

import WorkspaceInfo from '@/components/workspace/WorkspaceInfo'
import PlansUsed from '@/components/home/PlansUsed'
import CurrentMemberList from '@/components/home/CurrentMemberList'
import CurrentContentsList from '@/components/home/CurrentContentsList'
import CurrentResultList from '@/components/home/CurrentResultList'
import UserProfileCard from '@/components/home/UserProfileCard'
import LinkListCard from '@/components/home/LinkListCard'

import { install, guide } from '@/models/home'
import workspaceService from '@/services/workspace'

export default {
  components: {
    WorkspaceInfo,
    PlansUsed,
    CurrentMemberList,
    CurrentContentsList,
    CurrentResultList,
    UserProfileCard,
    LinkListCard,
  },
  data() {
    return {
      install: install(this),
      guide: guide(this),
      plansInfo: {
        storage: {},
        viewCount: {},
        callTime: {},
      },
    }
  },
  computed: {
    ...mapGetters({
      activeWorkspace: 'auth/activeWorkspace',
    }),
  },
  methods: {
    async getWorkspacePlansInfo() {
      if (this.activeWorkspace.role !== 'MASTER') return false
      this.plansInfo = await workspaceService.getWorkspacePlansInfo()
    },
  },
  async beforeMount() {
    this.getWorkspacePlansInfo()
    workspaceService.watchActiveWorkspace(this, this.getWorkspacePlansInfo)
  },
}
</script>

<style lang="scss">
#home .container {
  .main-banner {
    height: 190px;
    padding: 12px 8px;
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
  .faq-banner {
    position: relative;
    height: 116px;
    color: #fff;
    background: url('~assets/images/img-faq.jpg');
    background-size: cover;
    border: none;

    .el-card__body {
      padding: 20px;
      &:after {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-image: linear-gradient(200deg, #fff, transparent);
        opacity: 0;
        transition: 0.3s;
        content: '';
      }
      &:hover:after {
        opacity: 0.3;
      }
    }
    & h6 {
      font-size: 16px;
    }
    & p {
      margin-top: 12px;
      font-size: 13px;
      opacity: 0.9;
    }
    .el-icon-right {
      float: right;
      font-size: 20px;
      line-height: 26px;
    }
  }
  .el-card--table .el-table__body-wrapper,
  .el-card--table .el-table__empty-block {
    min-height: 256px;
  }
}
</style>
