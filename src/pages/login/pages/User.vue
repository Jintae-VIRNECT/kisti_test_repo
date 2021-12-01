<template>
  <div class="container">
    <el-row type="flex" justify="center" align="middle" class="row-bg">
      <el-col>
        <h2 class="title">{{ $t('user.title') }}</h2>
        <p class="disc" v-html="$t('user.pageInfo')"></p>

        <p class="input-title">{{ $t('user.profileImage.title') }}</p>
        <article class="profile-image">
          <div class="image-holder" @click="profilePopup = true">
            <div
              class="image"
              v-if="thumbnail"
              :style="`background-image: url(${thumbnail})`"
            ></div>
            <i class="camera-ico"
              ><img src="~assets/images/common/ic-camera-alt@2x.png"
            /></i>
          </div>
          <div class="text-wrap">
            <p>{{ $t('user.profileImage.subTitle') }}</p>
            <p>{{ $t('user.profileImage.contents') }}</p>
          </div>
        </article>
        <ThumbnailUpload
          :profilePopup="profilePopup"
          :inputImg="inputImg"
          :thumbnailImageBtnDisabled="thumbnailImageBtnDisabled"
          ref="imgUpload"
          @uploadPopupOpen="uploadPopupOpen"
          @uploadPopupClose="uploadPopupClose"
          @uploadImage="uploadImage"
          @imageUploadBtn="imageUploadBtn"
          @deleteImage="deleteImage"
          @thumbnailImageRegistBtn="thumbnailImageRegistBtn"
        />

        <p class="input-title">{{ $t('user.nickName.title') }}</p>
        <ValidationProvider rules="nickname" v-slot="{ errors }">
          <el-input
            v-model="user.nickname"
            :placeholder="nicknameSet"
            type="text"
            :class="{ 'input-danger': errors[0] }"
            clearable
          >
          </el-input>
        </ValidationProvider>
        <p class="restriction-text" v-html="$t('user.nickName.contents')"></p>

        <dl class="recover-info">
          <dt>{{ $t('user.recoveryInfo.title') }}</dt>
          <dd>{{ $t('user.recoveryInfo.placeHolder') }}</dd>
        </dl>

        <p class="input-title">{{ $t('user.phoneNumber.title') }}</p>
        <el-select
          v-model="userCountryCode"
          placeholder="+82"
          v-validate="'required'"
          class="countrycode-input"
          name="countryCode"
        >
          <el-option
            v-for="item in countryCodeLists"
            :key="item.value"
            :label="item.value"
            :value="item.value"
          >
          </el-option>
        </el-select>

        <el-input
          class="phonenumber-input"
          :placeholder="$t('user.phoneNumber.contents')"
          v-model="user.mobile"
          clearable
          type="text"
          name="mobile"
        ></el-input>

        <p class="input-title">{{ $t('user.recoveryEmail.title') }}</p>
        <el-input
          :placeholder="$t('user.recoveryEmail.placeHolder')"
          v-model="user.recoveryEmail"
          type="email"
          name="recoveryEmail"
          clearable
          v-validate="'email|max:50'"
          :class="{ 'input-danger': errors.has('recoveryEmail') }"
        >
        </el-input>

        <el-button
          class="next-btn block-btn"
          type="info"
          @click="checkNickName()"
          :disabled="
            errors.has('recoveryEmail') || !nicknameCheck || !checkConfirm
          "
          >{{ $t('user.confirm') }}</el-button
        >
        <el-button class="block-btn" @click="later">{{
          $t('user.later')
        }}</el-button>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import userSlice from 'service/slice/user.slice'
import mixin from 'mixins/mixin'
import { ValidationProvider } from 'vee-validate'
import ThumbnailUpload from 'components/popup/ThumbnailUpload'

export default {
  name: 'user',
  mixins: [mixin],
  props: {
    signup: Object,
  },
  emits: [
    'uploadPopupOpen',
    'uploadPopupClose',
    'uploadImage',
    'imageUploadBtn',
    'thumbnailImageRegistBtn',
  ],
  components: {
    ValidationProvider,
    ThumbnailUpload,
  },
  setup(props, { root }) {
    const USER_SLICE = userSlice(props, root)
    return {
      ...USER_SLICE,
    }
  },
}
</script>

<style lang="scss">
.el-dialog {
  border: solid 1px #e6e9ee;
  border-radius: 4px;
  box-shadow: 0 2px 8px 0 rgba(8, 23, 48, 0.06),
    0 2px 4px 0 rgba(8, 23, 48, 0.1);

  .image-holder {
    float: none;
    width: 160px;
    height: 160px;
    margin: 44px auto 16px;
    img {
      width: 100%;
      height: 100%;
      -webkit-mask: url('~assets/images/common/ic-bg.svg') no-repeat;
      -webkit-mask-size: 100%;
      mask: url('~assets/images/common/ic-bg.svg') no-repeat;
      mask-size: 100%;
    }

    @media (max-width: $mobile) {
      width: 30vw;
      height: 30vw;
      margin: 16px auto;
    }
  }
  &__wrapper {
    text-align: left;
  }
  input[type='file'] {
    position: absolute;
    top: 0;
    left: -9999px;
  }
  label {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    cursor: pointer;
  }
  .el-button {
    height: 36px;
    font-weight: normal;
    font-size: 13px;
    &.left-btn {
      float: left;
      @media (max-width: $mobile) {
        display: block;
        float: none;
        margin: 0 auto 24px;
      }
    }
  }
}
.el-button.next-btn {
  margin-top: 60px;
}
.el-upload__tip {
  color: #566173;
  font-size: 13px;
  text-align: center;
}
.image-holder {
  position: relative;
  float: left;
  width: 80px;
  height: 80px;
  margin: 20px 16px 0;
  text-align: center;
  background: url('~assets/images/common/ic-bg.svg') no-repeat;
  background-size: 100%;
  cursor: pointer;
  &:before {
    position: absolute;
    top: 0;
    left: 0;
    display: block;
    width: 80%;
    height: 80%;
    margin: 10%;
    background: url('~assets/images/common/ic-user-profile.svg') no-repeat;
    background-size: 100%;
    content: '';
  }
  .image {
    width: 100%;
    height: 100%;
    background-position: center;
    background-size: cover;
    -webkit-mask: url('~assets/images/common/ic-bg.svg') no-repeat;
    -webkit-mask-size: 100%;
    mask: url('~assets/images/common/ic-bg.svg') no-repeat;
    mask-size: 100%;
  }
  // .avatar {
  // 	max-width: 80%;
  // }
}
</style>
