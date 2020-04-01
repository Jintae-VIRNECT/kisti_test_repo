<template>
  <div id="home">
    <div class="home__bg"></div>
    <div class="container">
      <el-row>
        <section class="page-description">
          <div class="avatar">
            <img src="~assets/images/ic-user-profile.svg" />
          </div>
          <h2>사용자 닉네임님, 반갑습니다</h2>
          <p>
            개인 정보 보호 및 보안 설정을 관리하여 <br />
            VIRNECT 제품과 서비스를 이용할 수 있습니다.
          </p>
        </section>
      </el-row>
      <el-row>
        <el-col class="container__left">
          <el-card class="home__workspace-info">
            <div slot="header">
              <h3>나의 워크스페이스 정보</h3>
              <router-link to="/workspace">바로가기</router-link>
            </div>
            <dl>
              <dt>워크스페이스 이름</dt>
              <dd>VIRNECT’s workspace</dd>
            </dl>
            <dl>
              <dt>워크스페이스 설명</dt>
              <dd class="desc">워크스페이스 설명이 없습니다.</dd>
            </dl>
          </el-card>
          <el-card class="el-card--table">
            <div slot="header">
              <h3>구독 결제 정보</h3>
              <router-link to="/payment">바로가기</router-link>
            </div>
            <subscribe-payment-info />
          </el-card>
        </el-col>
        <el-col class="container__right">
          <el-card class="el-card--table">
            <div slot="header">
              <h3>사용 중인 플랜</h3>
              <span>회원님이 사용 중인 플랜 정보입니다.</span>
              <router-link to="/plan">바로가기</router-link>
            </div>
            <used-plan-list />
          </el-card>
          <el-card class="el-card--table">
            <div slot="header">
              <h3>나의 워크스페이스 플랜</h3>
              <span>회원님이 구매한 플랜 정보입니다.</span>
              <router-link to="/plan">바로가기</router-link>
            </div>
            <workspace-plan-list />
          </el-card>
        </el-col>
      </el-row>
      <el-row>
        <el-card class="el-card--table">
          <div slot="header">
            <h3>로그인 된 기기</h3>
            <span>내 계정으로 로그인된 디바이스 정보입니다.</span>
            <router-link to="/security">바로가기</router-link>
          </div>
          <logged-in-device-list />
        </el-card>
      </el-row>
    </div>
  </div>
</template>

<script>
import SubscribePaymentInfo from '@/components/payment/SubscribePaymentInfo'
import UsedPlanList from '@/components/plan/UsedPlanList'
import WorkspacePlanList from '@/components/plan/WorkspacePlanList'
import LoggedInDeviceList from '@/components/security/LoggedInDeviceList'

export default {
  components: {
    UsedPlanList,
    WorkspacePlanList,
    LoggedInDeviceList,
    SubscribePaymentInfo,
  },
}
</script>

<style lang="scss">
.home__bg {
  position: absolute;
  width: 100%;
  height: 420px;
  background: #0b2c63;
}
.page-description {
  margin-bottom: 60px;
  color: #fff;
  text-align: center;

  .avatar {
    width: 100px;
    height: 100px;
    margin: 0 auto;
    background: url('~assets/images/ic-bg.svg') no-repeat;
    background-size: 100%;
    & > img {
      width: 80%;
      height: 80%;
      margin: 10%;
    }
  }
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
