<template>
  <div id="home">
    <div class="home__bg"></div>
    <div class="container">
      <el-row>
        <section class="page-description">
          <div class="avatar"></div>
          <h2>{{ $t('home.title.welcome', { username: me.name }) }}</h2>
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
              <router-link to="/payment">{{ $t('common.link') }}</router-link>
            </div>
            <purchases-info />
          </el-card>
        </el-col>
        <el-col class="container__right">
          <el-card class="el-card--table">
            <div slot="header">
              <h3>{{ $t('home.usingPlan.title') }}</h3>
              <span>{{ $t('home.usingPlan.desc') }}</span>
              <router-link to="/plan">{{ $t('common.link') }}</router-link>
            </div>
          </el-card>
          <el-card class="el-card--table">
            <div slot="header">
              <h3>{{ $t('home.workspacePlan.title') }}</h3>
              <span>{{ $t('home.workspacePlan.desc') }}</span>
              <router-link to="/plan">{{ $t('common.link') }}</router-link>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-row>
        <el-card class="el-card--table">
          <div slot="header">
            <h3>{{ $t('home.LoggedInDevice.title') }}</h3>
            <span>{{ $t('home.LoggedInDevice.desc') }}</span>
            <router-link to="/security">{{ $t('common.link') }}</router-link>
          </div>
          <logged-in-device-list />
        </el-card>
      </el-row>
    </div>
  </div>
</template>

<script>
import WorkspaceInfo from '@/components/workspace/WorkspaceInfo'
import PurchasesInfo from '@/components/purchases/PurchasesInfo'
import LoggedInDeviceList from '@/components/security/LoggedInDeviceList'

export default {
  components: {
    WorkspaceInfo,
    LoggedInDeviceList,
    PurchasesInfo,
  },
  data() {
    return {
      me: {
        name: '사용자 닉네임',
      },
    }
  },
}
</script>

<style lang="scss">
#home .container {
  padding-top: 73px;
  .el-table__empty-block {
    min-height: 300px;
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

  & > h2 {
    margin: 12px;
    font-size: 28px;
  }
  & > p {
    font-size: 15px;
    line-height: 1.6;
    opacity: 0.9;
  }
}
</style>
