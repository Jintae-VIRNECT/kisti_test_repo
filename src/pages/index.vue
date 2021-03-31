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
              <WorkspaceInfo />
            </el-card>
          </el-row>
          <el-row v-if="!$isOnpremise">
            <HomePlansUsed
              i18nGroup="home.plansInfo.arStorage"
              :info="plansInfo.storage"
            />
            <HomePlansUsed
              i18nGroup="home.plansInfo.arContent"
              :info="plansInfo.viewCount"
            />
            <!-- <HomePlansUsed
              i18nGroup="home.plansInfo.call"
              :info="plansInfo.callTime"
            /> -->
          </el-row>
        </el-col>
        <!-- 가운데 -->
        <el-col class="container__center">
          <el-card class="main-banner">
            <h6>{{ $t('home.banner.sub') }}</h6>
            <h5>{{ $t('home.banner.main', { company }) }}</h5>
            <p v-if="$isOnpremise">
              {{ $t('home.banner.desc_op', { company }) }}
            </p>
            <p v-else>
              {{ $t('home.banner.desc') }}
            </p>
          </el-card>
          <HomeMemberList v-if="!$isOnpremise" />
          <HomeContentsList />
          <HomeResultList />
        </el-col>
        <!-- 오른쪽 -->
        <el-col class="container__right">
          <HomeUserProfileCard />
          <HomeDownloadCenter v-if="!$isOnpremise" />
          <HomeGuideList v-if="!$isOnpremise" />
          <a
            v-if="!$isOnpremise"
            :href="`${$url.www}/support/faq`"
            target="_blank"
          >
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

    <HomeAlertStorageOverflow :visible.sync="showAlertStorageOverflow" />
    <HomeAlertLicenseOverflow :visible.sync="showAlertLicenseOverflow" />
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import workspaceService from '@/services/workspace'

export default {
  data() {
    return {
      showAlertStorageOverflow: false,
      showAlertLicenseOverflow: false,
    }
  },
  computed: {
    ...mapGetters({
      activeWorkspace: 'auth/activeWorkspace',
      plansInfo: 'plan/plansInfo',
      company: 'layout/title',
    }),
  },
  methods: {
    async getWorkspacePlansInfo() {
      const plansInfo = await this.$store.dispatch('plan/getPlansInfo')
      if (plansInfo.storage.remain < 0) {
        this.showAlertStorageOverflow = true
      }
      if (plansInfo.products.some(p => p.usedAmount > p.amount)) {
        this.showAlertLicenseOverflow = true
      }
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
      font-family: $font-poppins, $font-noto;
    }
    & h5 {
      font-weight: bold;
      font-size: 28px;
      font-family: $font-poppins, $font-noto;
    }
    & p {
      margin-top: 20px;
      font-size: 20px;
      opacity: 0.9;
    }
  }
  .faq-banner {
    position: relative;
    min-height: 116px;
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

  .link-list-card {
    .el-card__body {
      padding: 8px 0;
    }
    a {
      position: relative;
      display: flex;
      align-items: center;
      height: 44px;
      padding: 0 24px;
      transition: background-color 0.25s ease;
      &:hover {
        background-color: #f5f7fa;
      }

      & > img:first-child {
        margin-right: 4px;
        margin-left: -8px;
      }
      & > span {
        color: $font-color-content;
      }
      & > img:last-child {
        position: absolute;
        right: 24px;
      }
    }
  }
}
</style>
