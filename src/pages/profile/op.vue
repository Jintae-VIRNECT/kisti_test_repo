<template>
  <div id="profile">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>{{ $t('menu.account') }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{ $t('menu.profile_op') }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('menu.profile_op') }}</h2>
        <p>{{ $t('profile_op.desc') }}</p>
      </div>
      <!-- 프로필, 닉네임 -->
      <el-card class="designed">
        <div slot="header">
          <h3>{{ $t('profile.info.title') }}</h3>
          <span>{{ $t('profile_op.info.desc') }}</span>
        </div>
        <div>
          <div class="profile__info">
            <h4>{{ $t('profile.info.title') }}</h4>
            <div class="content">
              <span class="desc">
                {{ $t('profile_op.info.desc') }}
              </span>
            </div>
            <div class="avatar" @click="visible.imageChangeModal = true">
              <VirnectThumbnail :size="120" :image="cdn(profileImg)" />
              <i>
                <img src="~assets/images/icon/ic-camera-alt.svg" />
              </i>
            </div>
          </div>
          <div class="profile__info">
            <h4>{{ $t('profile.info.nickname') }}</h4>
            <div class="content">
              <span class="value">{{ me.nickname }}</span>
              <span class="desc">
                {{ $t('profile_op.info.nicknameDesc') }}
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
        </div>
        <div>
          <div class="profile__info">
            <h4>{{ $t('profile.account.email') }}</h4>
            <div class="content">
              <span class="value">{{ me.email }}</span>
              <span class="desc">
                {{ $t('profile_op.account.emailDesc') }}
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
            <h4>{{ $t('profile_op.account.passwordQuestion') }}</h4>
            <div class="content">
              <span class="value">{{ me.question }}</span>
            </div>
            <el-button
              type="text"
              @click="visible.passwordQuestionModal = true"
            >
              {{ $t('common.change') }}
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
    <ProfilePasswordQuestionModal
      :me="me"
      :visible.sync="visible.passwordQuestionModal"
      @changedPasswordQuestion="changedPasswordQuestion"
    />
  </div>
</template>

<script>
import { filters } from '@/plugins/dayjs'
import profileService from '@/services/profile'
import filterMixin from '@/mixins/filters'

export default {
  mixins: [filterMixin],
  middleware: ['default', 'profile'],
  filters: {
    ...filters,
  },
  data() {
    return {
      me: profileService.getMyProfile(),
      visible: {
        imageChangeModal: false,
        nicknameChangeModal: false,
        passwordChangeModal: false,
        passwordQuestionModal: false,
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
      this.$store.dispatch('auth/getAuth', this.$config.VIRNECT_ENV)
    },
    changedNickname(nickname) {
      this.me.nickname = nickname
      this.visible.nicknameChangeModal = false
      this.$store.dispatch('auth/getAuth', this.$config.VIRNECT_ENV)
    },
    changedPassword() {
      this.visible.passwordChangeModal = false
    },
    changedPasswordQuestion(form) {
      this.me.question = form.question
      this.me.answer = form.answer
      this.visible.passwordQuestionModal = false
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
