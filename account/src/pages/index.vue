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
          <el-card class="home__workspace-info">
            <div slot="header">
              <h3>{{ $t('home.workspace.title') }}</h3>
              <router-link to="/workspace">{{ $t('common.link') }}</router-link>
            </div>
            <dl>
              <dt>{{ $t('home.workspace.name') }}</dt>
              <dd>{{ workspace.name }}</dd>
            </dl>
            <dl>
              <dt>{{ $t('home.workspace.desc') }}</dt>
              <dd class="desc">
                {{ workspace.desc || $t('home.workspace.descEmpty') }}
              </dd>
            </dl>
          </el-card>
          <el-card class="el-card--table">
            <div slot="header">
              <h3>{{ $t('home.payment.title') }}</h3>
              <router-link to="/payment">{{ $t('common.link') }}</router-link>
            </div>
            <subscribe-payment-info />
          </el-card>
        </el-col>
        <el-col class="container__right">
          <el-card class="el-card--table">
            <div slot="header">
              <h3>{{ $t('home.usingPlan.title') }}</h3>
              <span>{{ $t('home.usingPlan.desc') }}</span>
              <router-link to="/plan">{{ $t('common.link') }}</router-link>
            </div>
            <using-plan-list />
          </el-card>
          <el-card class="el-card--table">
            <div slot="header">
              <h3>{{ $t('home.workspacePlan.title') }}</h3>
              <span>{{ $t('home.workspacePlan.desc') }}</span>
              <router-link to="/plan">{{ $t('common.link') }}</router-link>
            </div>
            <workspace-plan-list />
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
import SubscribePaymentInfo from '@/components/payment/SubscribePaymentInfo'
import UsingPlanList from '@/components/plan/UsingPlanList'
import WorkspacePlanList from '@/components/plan/WorkspacePlanList'
import LoggedInDeviceList from '@/components/security/LoggedInDeviceList'

export default {
  components: {
    UsingPlanList,
    WorkspacePlanList,
    LoggedInDeviceList,
    SubscribePaymentInfo,
  },
  data() {
    return {
      me: {
        name: '사용자 닉네임',
      },
      workspace: {
        name: 'VIRNECT’s workspace',
        desc: '',
      },
    }
  },
}
</script>

<style lang="scss">
#home .container {
  padding-top: 80px;
  .el-table__empty-block {
    min-height: 300px;
  }
}
.home__bg {
  position: absolute;
  width: 100%;
  height: 420px;
  background: url('~assets/images/bg_profile.jpg') center no-repeat;
  background-size: 100%;
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
.home__workspace-info {
  .el-card__body > dl {
    padding: 12px 0;

    dt {
      color: $font-color-desc;
      font-size: 12px;
    }
    dd {
      margin-top: 4px;
      font-size: 20px;
    }
    dd.desc {
      margin-top: 8px;
      font-size: inherit;
    }
  }
}
</style>
