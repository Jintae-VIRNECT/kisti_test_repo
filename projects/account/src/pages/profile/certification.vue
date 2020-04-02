<template>
  <div id="certification">
    <div class="container">
      <el-card class="virnect-login-form">
        <div>
          <p v-html="$t('certification.desc')"></p>
          <el-image :src="me.image"></el-image>
          <span class="name">{{ me.name }}</span>
          <span class="email">{{ me.email }}</span>
          <el-form>
            <el-form-item label="Activity name">
              <el-input v-model="form.password"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="onSubmit">
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
  data() {
    return {
      me: profileService.getMyProfile(),
      form: {
        password: null,
      },
    }
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
    padding: 40px;
    p {
      padding: 4px 0 8px;
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
  .el-image {
    width: 100px;
    height: 100px;
    margin: 20px;
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
