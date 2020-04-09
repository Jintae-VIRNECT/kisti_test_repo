<template>
  <div id="certification">
    <div class="container">
      <el-card class="virnect-login-form">
        <div>
          <p v-html="$t('certification.desc')"></p>
          <div class="avatar"></div>
          <span class="name">{{ me.lastName }} {{ me.firstName }}</span>
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
        </div>
      </el-card>
      <div class="bottom">
        <a href="http://console.virnect.com/password">
          {{ $t('certification.forgetPassword') }}
        </a>
      </div>
    </div>
  </div>
</template>

<script>
import profileService from '@/services/profile'

export default {
  layout: 'noSidebar',
  data() {
    return {
      me: {
        lastName: 'SMIC',
        firstName: 'MASTER',
        email: 'smic1',
      },
      form: {
        uuid: '498b1839dc29ed7bb2ee90ad6985c608',
        email: 'smic1',
        password: null,
      },
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
    p {
      font-size: 16px;
    }
    span {
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
