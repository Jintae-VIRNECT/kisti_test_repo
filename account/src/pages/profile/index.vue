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
              <img :src="me.image" />
              <i>
                <img src="~assets/images/ic-camera-alt.svg" />
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
          <span>{{ $t('profile.account.desc') }}</span>
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
        </div>
      </el-card>
      <!-- 사용자 추가 정보 -->
      <el-card>
        <div slot="header">
          <h3>{{ $t('profile.additional.title') }}</h3>
          <span>{{ $t('profile.additional.desc') }}</span>
        </div>
        <div>
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
    <image-change-modal
      :me="me"
      :visible.sync="visible.imageChangeModal"
      @changedImage="changedImage"
    />
    <name-change-modal
      :me="me"
      :visible.sync="visible.nameChangeModal"
      @changedName="changedName"
    />
    <nickname-change-modal
      :me="me"
      :visible.sync="visible.nicknameChangeModal"
      @changedNickname="changedNickname"
    />
    <password-change-modal
      :me="me"
      :visible.sync="visible.passwordChangeModal"
      @changedPassword="changedPassword"
    />
    <birth-change-modal
      :me="me"
      :visible.sync="visible.birthChangeModal"
      @changedBirth="changedBirth"
    />
    <contact-change-modal
      :me="me"
      :visible.sync="visible.contactChangeModal"
      @changedContact="changedContact"
    />
  </div>
</template>

<script>
import { filters } from '@/plugins/dayjs'
import profileService from '@/services/profile'

import ImageChangeModal from '@/components/profile/ImageChangeModal'
import NameChangeModal from '@/components/profile/NameChangeModal'
import NicknameChangeModal from '@/components/profile/NicknameChangeModal'
import PasswordChangeModal from '@/components/profile/PasswordChangeModal'
import BirthChangeModal from '@/components/profile/BirthChangeModal'
import ContactChangeModal from '@/components/profile/ContactChangeModal'

export default {
  middleware: 'auth',
  components: {
    ImageChangeModal,
    NameChangeModal,
    NicknameChangeModal,
    PasswordChangeModal,
    BirthChangeModal,
    ContactChangeModal,
  },
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
      },
    }
  },
  computed: {
    myBirth() {
      if (this.me.birth) return filters.localDateFormat(this.me.birth)
      else return this.$t('profile.additional.birthEmpty')
    },
  },
  methods: {
    changedImage(image) {
      this.me.image = image
      this.visible.imageChangeModal = false
    },
    changedName({ lastName, firstName }) {
      this.me.lastName = lastName
      this.me.firstName = firstName
      this.visible.nameChangeModal = false
    },
    changedNickname(nickname) {
      this.me.nickname = nickname
      this.visible.nicknameChangeModal = false
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
    background: url('~assets/images/bg@2x.jpg') center no-repeat;
    background-size: 200%;

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
      font-size: 12.6px;
    }
  }
  .el-button {
    margin-top: 0;
    color: $color-link;
  }
}
</style>
