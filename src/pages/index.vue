<template>
  <div id="home">
    <div class="home__bg"></div>
    <div class="container">
      <el-row>
        <section class="page-description">
          <VirnectThumbnail :size="100" :image="me.image" />
          <h2>{{ $t('home.title.welcome', { username: me.nickname }) }}</h2>
          <p v-html="$t('home.title.description')"></p>
        </section>
      </el-row>
      <el-row>
        <el-col class="container__left">
          <el-card>
            <div slot="header">
              <h3>{{ $t('home.workspace.title') }}</h3>
              <router-link to="/workspace">{{ $t('common.link') }}</router-link>
            </div>
            <workspace-info />
          </el-card>
          <el-card>
            <div slot="header">
              <h3>{{ $t('home.payment.title') }}</h3>
              <router-link to="/purchases">{{ $t('common.link') }}</router-link>
            </div>
            <purchases-info
              simple
              :plansInfo="plansInfo"
              :paymentInfo="paymentInfo"
            />
          </el-card>
        </el-col>
        <el-col class="container__right">
          <!-- 워크스페이스 리스트 -->
          <el-card class="el-card--table">
            <div slot="header">
              <h3>{{ $t('workspace.list.title') }}</h3>
              <router-link to="/workspace">{{ $t('common.link') }}</router-link>
            </div>
            <workspace-list :isHome="true" />
          </el-card>
          <!-- 플랜 리스트 -->
          <el-card class="el-card--table">
            <div slot="header">
              <h3>{{ $t('workspace.usingPlanList.title') }}</h3>
              <router-link to="/workspace">{{ $t('common.link') }}</router-link>
            </div>
            <using-plan-list :isHome="true" />
          </el-card>
          <!-- 로그인된 기기 -->
          <el-card class="el-card--table">
            <div slot="header">
              <h3>{{ $t('home.LoggedInDevice.title') }}</h3>
              <router-link to="/security">{{ $t('common.link') }}</router-link>
            </div>
            <logged-in-device-list :isHome="true" />
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import WorkspaceInfo from '@/components/workspace/WorkspaceInfo'
import PurchasesInfo from '@/components/purchases/PurchasesInfo'
import WorkspaceList from '@/components/workspace/WorkspaceList'
import UsingPlanList from '@/components/workspace/UsingPlanList'
import LoggedInDeviceList from '@/components/security/LoggedInDeviceList'
import purchaseService from '@/services/purchases'
import paymentService from '@/services/payment'
import profileService from '@/services/profile'

export default {
  components: {
    WorkspaceInfo,
    WorkspaceList,
    UsingPlanList,
    LoggedInDeviceList,
    PurchasesInfo,
  },
  async asyncData() {
    const [plansInfo, paymentInfo] = await Promise.all([
      purchaseService.getWorkspacePlansInfo(),
      paymentService.getAutoPayments(),
    ])

    return { plansInfo, paymentInfo }
  },
  data() {
    return {
      me: profileService.getMyProfile(),
    }
  },
}
</script>

<style lang="scss">
#home .container {
  padding-top: 73px;
  .el-table__empty-block,
  .el-card--table .el-card__body .el-table__body-wrapper {
    min-height: 320px;
  }
}
.home__bg {
  position: absolute;
  width: 100%;
  height: 420px;
  background: url('~assets/images/bg_profile.jpg') center no-repeat;
  background-size: cover;
}
.page-description {
  margin-bottom: 60px;
  color: #fff;
  text-align: center;

  .thumbnail {
    margin: 0 auto;
  }
  & > h2 {
    margin: 12px;
    overflow: hidden;
    font-size: 28px;
    text-overflow: ellipsis;
  }
  & > p {
    font-size: 15px;
    line-height: 1.6;
    opacity: 0.9;
  }
}
</style>
