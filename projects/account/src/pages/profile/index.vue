<template>
  <div id="profile">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>계정 관리</el-breadcrumb-item>
          <el-breadcrumb-item>개인 정보</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>개인 정보</h2>
        <p>VIRNECT 제품과 서비스에서 사용되는 기본 정보를 관리합니다.</p>
      </div>
      <el-card class="designed">
        <div slot="header">
          <h3>프로필</h3>
          <span>회원님의 프로필 정보를 관리합니다.</span>
        </div>
        <div>
          <div class="profile__info">
            <h4>프로필 이미지</h4>
            <div class="content">
              <span class="desc">
                프로필 이미지를 등록하시면 다른 사용자가 나를 더 쉽게 찾을 수
                있습니다.
              </span>
            </div>
            <div class="thumbnail">
              <img src="~assets/images/img-user-profile-08.png" />
              <i>
                <img src="~assets/images/ic-camera-alt.svg" />
              </i>
            </div>
          </div>
          <div class="profile__info">
            <h4>이름</h4>
            <div class="content">
              <span class="value">장 선영</span>
              <span class="desc">
                회원님의 이름은 결재 및 구매 관리, 계정 정보 보안 관리에
                사용됩니다.
              </span>
            </div>
            <el-button type="text">이름 변경</el-button>
          </div>
          <div class="profile__info">
            <h4>닉네임</h4>
            <div class="content">
              <span class="value">버넥트 연구원</span>
              <span class="desc">
                회원님의 닉네임은 VIRNECT 서비스를 사용하는 다른 사용자에게
                보여집니다.
              </span>
            </div>
            <el-button type="text">닉네임 변경</el-button>
          </div>
        </div>
      </el-card>
      <el-card>
        <div slot="header">
          <h3>계정 정보</h3>
          <span>회원님의 계정과 비밀번호 정보를 관리합니다.</span>
        </div>
        <div>
          <div class="profile__info">
            <h4>계정</h4>
            <div class="content">
              <span class="value">example@example.com</span>
              <span class="desc">
                가입 시 등록한 이메일 주소는 변경할 수 없습니다.
              </span>
            </div>
          </div>
          <div class="profile__info">
            <h4>비밀번호</h4>
            <div class="content">
              <span class="value password">·······</span>
              <span class="desc">
                주기적인 비밀번호 변경은 보안 강화에 도움이 됩니다.
              </span>
            </div>
            <el-button type="text">비밀번호 변경</el-button>
          </div>
        </div>
      </el-card>
      <el-card>
        <div slot="header">
          <h3>사용자 추가 정보</h3>
          <span>
            사용자 추가 정보는 계정 이메일 분실 시 사용자 본인 인증을 위해
            활용됩니다.
          </span>
        </div>
        <div>
          <div class="profile__info">
            <h4>생년월일</h4>
            <div class="content">
              <span class="value">등록된 생년월일이 없습니다.</span>
            </div>
            <el-button type="text">생년월일 변경</el-button>
          </div>
          <div class="profile__info">
            <h4>연락처</h4>
            <div class="content">
              <span class="value">등록된 연락처가 없습니다.</span>
            </div>
            <el-button type="text">연락처 변경</el-button>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
export default {}
</script>

<style lang="scss">
#profile {
  .el-card__header {
    padding-right: 50px;
    padding-left: 40px;
  }
  .el-card__body {
    padding: 0;
  }
}
#profile .designed {
  .el-card__header {
    height: 240px;
    padding: 36px 40px;
    background: #0b2c63;

    & > div > h3 {
      display: block;
      float: none;
      color: #fff;
      font-size: 20px;
    }
    & > div > span {
      margin: 12px 0;
      color: #fff;
    }
  }
  .thumbnail {
    position: relative;
    top: -54px;
    cursor: pointer;
    & > i {
      position: absolute;
      right: -4px;
      bottom: 4px;
      width: 32px;
      height: 32px;
      background: #fff;
      border-radius: 32px;
      img {
        position: absolute;
        top: 0;
        right: 0;
        bottom: 0;
        left: 0;
        margin: auto;
      }
    }
  }
}
#profile .profile__info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 110px;
  padding: 0 40px;

  &:not(:last-child) {
    border-bottom: 1px solid #ebeef5;
  }

  h4 {
    width: 220px;
    color: $font-color-content;
  }
  .content {
    flex-grow: 1;
    & > span {
      display: block;
      margin: 8px 0;
    }
    .value {
      color: $font-color-content;
      font-size: 16px;
    }
    .value.password {
      margin: -0.5em 0;
      font-size: 40px;
      letter-spacing: 0.1em;
    }
    .desc {
      color: $font-color-desc;
      font-size: 12px;
    }
  }
  .el-button {
    margin-top: 0;
  }
}
</style>
