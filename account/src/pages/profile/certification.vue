<template>
  <div id="certification">
    <div class="container">
      <el-card class="virnect-login-form">
        <p v-html="$t('certification.desc')"></p>
        <div class="avatar">
          <div
            class="image"
            v-if="me.image"
            :style="`background-image: url('${me.image}')`"
          />
        </div>
        <span class="name">{{ me.nickname }}</span>
        <span class="email">{{ me.email }}</span>
        <el-form ref="form" :model="form" @submit.native.prevent="submit">
          <el-form-item :label="$t('certification.password')">
            <el-input
              :placeholder="$t('certification.passwordPlaceholder')"
              v-model="form.password"
              show-password
            ></el-input>
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              @click="submit"
              :disabled="!form.password"
            >
              {{ $t('common.confirm') }}
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
      <div class="bottom">
        <a :href="findPasswordUrl" target="_blank">
          {{ $t('certification.forgetPassword') }}
        </a>
      </div>
    </div>
  </div>
</template>

<script>
import profileService from '@/services/profile'
import urls from 'WC-Modules/javascript/api/virnectPlatform/urls'

export default {
  layout: 'noSidebar',
  data({ $config }) {
    const profile = profileService.getMyProfile()
    return {
      me: profile,
      form: {
        uuid: profile.uuid,
        email: profile.email,
        password: '',
      },
      findPasswordUrl: `${urls.console[$config.TARGET_ENV]}/find`,
    }
  },
  methods: {
    async submit() {
      try {
        await profileService.certification(this.form)
        this.$router.push(`/profile`)
      } catch (e) {
        console.error(e)
        const message = /^Error: 4001/.test(e)
          ? this.$t('certification.message.wrong')
          : this.$t('certification.message.fail')
        this.$message.error({
          message,
          showClose: true,
        })
      }
    },
  },
}
</script>

<style lang="scss">
#certification {
  .el-card {
    width: 460px;
    margin: 0 auto;
    color: $font-color-content;
    text-align: center;
  }
  .el-card__body {
    padding: 44px 40px 48px;
    & > p {
      font-size: 16px;
    }
    & > span {
      display: block;
    }
    .name {
      font-size: 24px;
    }
    .email {
      color: $font-color-desc;
    }
  }
  .avatar {
    margin: 28px auto 20px;
  }
  .el-form {
    margin-top: 40px;
    text-align: right;
    .el-input__suffix {
      margin-top: 2px;
    }
  }
  .bottom {
    margin: 24px;
    text-align: center;
    a {
      color: $font-color-content;
    }
  }
}
</style>
