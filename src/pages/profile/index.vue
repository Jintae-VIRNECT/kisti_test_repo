<template>
  <div id="profile">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item to="/">{{
            $t('menu.account')
          }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{ $t('menu.profile') }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('menu.profile') }}</h2>
        <p>{{ $t('profile.desc') }}</p>
      </div>
      <!-- 프로필, 이름, 닉네임 -->
      <el-card class="designed">
        <div slot="header">
          <h3>{{ $t('profile.info.title') }}</h3>
          <span>{{ $t('profile.info.desc') }}</span>
        </div>
        <div>
          <div class="profile__info">
            <h4>{{ $t('profile.info.title') }}</h4>
            <div class="content">
              <span class="desc">
                {{ $t('profile.info.desc') }}
              </span>
            </div>
            <div class="avatar" @click="visible.imageChangeModal = true">
              <VirnectThumbnail :size="120" :image="profileImg" />
              <i>
                <img src="~assets/images/icon/ic-camera-alt.svg" />
              </i>
            </div>
          </div>
          <div class="profile__info">
            <h4>{{ $t('profile.info.name') }}</h4>
            <div class="content">
              <span class="value">{{ me.lastName }} {{ me.firstName }}</span>
              <span class="desc">
                {{ $t('profile.info.nameDesc') }}
              </span>
            </div>
            <el-button type="text" @click="visible.nameChangeModal = true">
              {{ $t('profile.info.nameChange') }}
            </el-button>
          </div>
          <div class="profile__info">
            <h4>{{ $t('profile.info.nickname') }}</h4>
            <div class="content">
              <span class="value">{{ me.nickname }}</span>
              <span class="desc">
                {{ $t('profile.info.nicknameDesc') }}
              </span>
            </div>
            <el-button type="text" @click="visible.nicknameChangeModal = true">
              {{ $t('profile.info.nicknameChange') }}
            </el-button>
          </div>
        </div>
      </el-card>
      <!-- 계정 정보 -->
      <el-card>
        <div slot="header">
          <h3>{{ $t('profile.account.title') }}</h3>
          <el-tooltip
            :content="$t('profile.account.desc')"
            placement="bottom-start"
          >
            <img src="~assets/images/icon/ic-error.svg" />
          </el-tooltip>
        </div>
        <div>
          <div class="profile__info">
            <h4>{{ $t('profile.account.email') }}</h4>
            <div class="content">
              <span class="value">{{ me.email }}</span>
              <span class="desc">
                {{ $t('profile.account.emailDesc') }}
              </span>
            </div>
          </div>
          <div class="profile__info">
            <h4>{{ $t('profile.account.password') }}</h4>
            <div class="content">
              <span class="value password">··········</span>
              <span class="desc">
                {{ $t('profile.account.passwordDesc') }}
              </span>
            </div>
            <el-button type="text" @click="visible.passwordChangeModal = true">
              {{ $t('profile.account.passwordChange') }}
            </el-button>
          </div>
          <div class="profile__info">
            <h4>{{ $t('profile.additional.birth') }}</h4>
            <div class="content">
              <span class="value">
                {{ myBirth }}
              </span>
            </div>
            <el-button type="text" @click="visible.birthChangeModal = true">
              {{ $t('profile.additional.birthChange') }}
            </el-button>
          </div>
          <div class="profile__info">
            <h4>{{ $t('profile.account.marketing') }}</h4>
            <div class="content">
              <span>
                {{
                  me.marketInfoReceive == 'ACCEPT'
                    ? $t('profile.account.marketingAgree')
                    : $t('profile.account.marketingDisagree')
                }}
              </span>
            </div>
            <el-button
              type="text"
              @click="visible.MarketInfoReceiveModal = true"
            >
              {{ $t('profile.account.marketingChange') }}
            </el-button>
          </div>
          <div class="profile__info">
            <h4>{{ $t('profile.account.secession') }}</h4>
            <div class="content">
              <el-button type="info" @click="visible.SecessionModal = true">
                {{ $t('profile.account.secession') }}
              </el-button>
            </div>
          </div>
        </div>
      </el-card>
      <!-- 사용자 추가 정보 -->
      <el-card>
        <div slot="header">
          <h3>{{ $t('profile.additional.title') }}</h3>
          <el-tooltip
            :content="$t('profile.additional.desc')"
            placement="bottom-start"
          >
            <img src="~assets/images/icon/ic-error.svg" />
          </el-tooltip>
        </div>
        <div>
          <div class="profile__info">
            <h4>{{ $t('profile.additional.recoveryEmail') }}</h4>
            <div class="content">
              <span class="value">
                {{
                  me.recoveryEmail ||
                  $t('profile.additional.recoveryEmailEmpty')
                }}
              </span>
            </div>
            <el-button
              type="text"
              @click="visible.recoveryEmailChangeModal = true"
            >
              {{ $t('profile.additional.recoveryEmailChange') }}
            </el-button>
          </div>
          <div class="profile__info">
            <h4>{{ $t('profile.additional.contact') }}</h4>
            <div class="content">
              <span class="value">
                {{ me.contact || $t('profile.additional.contactEmpty') }}
              </span>
            </div>
            <el-button type="text" @click="visible.contactChangeModal = true">
              {{ $t('profile.additional.contactChange') }}
            </el-button>
          </div>
        </div>
      </el-card>
    </div>
    <!-- 모달 -->
    <ProfileImageModal
      :me="me"
      :visible.sync="visible.imageChangeModal"
      @changedImage="changedImage"
    />
    <ProfileNameModal
      :me="me"
      :visible.sync="visible.nameChangeModal"
      @changedName="changedName"
    />
    <ProfileNicknameModal
      :me="me"
      :visible.sync="visible.nicknameChangeModal"
      @changedNickname="changedNickname"
    />
    <ProfilePasswordModal
      :me="me"
      :visible.sync="visible.passwordChangeModal"
      @changedPassword="changedPassword"
    />
    <ProfileBirthModal
      :me="me"
      :visible.sync="visible.birthChangeModal"
      @changedBirth="changedBirth"
    />
    <ProfileContactModal
      :me="me"
      :visible.sync="visible.contactChangeModal"
      @changedContact="changedContact"
    />
    <ProfileRecoveryEmailModal
      :me="me"
      :visible.sync="visible.recoveryEmailChangeModal"
      @changedRecoveryEmail="changedRecoveryEmail"
    />
    <ProfileMarketInfoReceiveModal
      :me="me"
      :visible.sync="visible.MarketInfoReceiveModal"
      @changedMarketInfoReceive="changedMarketInfoReceive"
    />
    <ProfileSecessionModal
      :me="me"
      :visible.sync="visible.SecessionModal"
      @changedMarketInfoReceive="changedMarketInfoReceive"
    />
  </div>
</template>

<script>
import { filters } from '@/plugins/dayjs'
import profileService from '@/services/profile'

export default {
  middleware: ['default', 'profile'],
  filters: {
    ...filters,
  },
  data() {
    return {
      me: profileService.getMyProfile(),
      visible: {
        imageChangeModal: false,
        nameChangeModal: false,
        nicknameChangeModal: false,
        passwordChangeModal: false,
        birthChangeModal: false,
        contactChangeModal: false,
        recoveryEmailChangeModal: false,
        MarketInfoReceiveModal: false,
        SecessionModal: false,
      },
    }
  },
  computed: {
    profileImg() {
      if (this.me.profile) return this.me.profile
      else return require('assets/images/icon/ic-user-profile.png')
    },
    myBirth() {
      if (this.me.birth) return filters.fullYearDateFormat(this.me.birth)
      else return this.$t('profile.additional.birthEmpty')
    },
  },
  methods: {
    changedImage(image) {
      this.me.profile = image
      this.visible.imageChangeModal = false
      this.$store.dispatch('auth/getAuth')
    },
    changedName({ lastName, firstName }) {
      this.me.lastName = lastName
      this.me.firstName = firstName
      this.visible.nameChangeModal = false
    },
    changedNickname(nickname) {
      this.me.nickname = nickname
      this.visible.nicknameChangeModal = false
      this.$store.dispatch('auth/getAuth')
    },
    changedPassword() {
      this.visible.passwordChangeModal = false
    },
    changedBirth(birth) {
      this.me.birth = birth
      this.visible.birthChangeModal = false
    },
    changedContact(contact) {
      this.me.contact = contact
      this.visible.contactChangeModal = false
    },
    changedRecoveryEmail(email) {
      this.me.recoveryEmail = email
      this.visible.recoveryEmailChangeModal = false
    },
    changedMarketInfoReceive(marketInfoReceive) {
      this.me.marketInfoReceive = marketInfoReceive
      this.visible.MarketInfoReceiveModal = false
    },
  },
  beforeCreate() {
    this.$store.commit('auth/SET_AUTHENTICATED', false)
  },
}
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
    background: url('~assets/images/bg_profile.jpg') center no-repeat;
    border: none;

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
  .avatar {
    position: relative;
    top: -56px;
    width: 120px;
    height: 120px;
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
    overflow: hidden;
    & > span {
      display: block;
      margin: 8px 0;
      overflow: hidden;
      text-overflow: ellipsis;
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
      font-size: 13px;
    }
  }
  .el-button--text {
    margin-top: 0;
    color: $color-link;
  }
}
</style>
