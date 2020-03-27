<template>
  <div>
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
        <el-col class="home__container__left">
          <el-card>
            <div slot="header">
              <h3>나의 워크스페이스 정보</h3>
              <router-link to="/workspace">바로가기</router-link>
            </div>
            <div>
              asdf
            </div>
          </el-card>
          <el-card>
            <div slot="header">
              <h3>구독 결제 정보</h3>
              <router-link to="/payment">바로가기</router-link>
            </div>
            <div>
              asdf
            </div>
          </el-card>
        </el-col>
        <el-col class="home__container__right">
          <el-card>
            <div slot="header">
              <h3>사용 중인 플랜</h3>
              <router-link to="/plan">바로가기</router-link>
            </div>
            <div>
              asdf
            </div>
          </el-card>
          <el-card>
            <div slot="header">
              <h3>나의 워크스페이스 플랜</h3>
              <router-link to="/plan">바로가기</router-link>
            </div>
            <div>
              asdf
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-row>
        <el-card>
          <div slot="header">
            <h3>로그인 된 기기</h3>
            <router-link to="/security">바로가기</router-link>
          </div>
          <div>
            asdf
          </div>
        </el-card>
      </el-row>
    </div>
  </div>
</template>

<script>
export default {}
</script>

<style lang="scss">
.home__bg {
  position: fixed;
  width: 100%;
  height: 420px;
  background: #0b2c63;
}
.home__container__left {
  width: 340px;
  margin-right: 30px;
}
.home__container__right {
  width: 830px;
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
  }
}
</style>
