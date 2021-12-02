<template>
  <div class="container">
    <el-row type="flex" justify="center" align="middle" class="row-bg">
      <el-col>
        <h2 class="title">{{ $t('login.title') }}</h2>
        <p class="input-title">
          {{ $env !== 'onpremise' ? $t('login.email') : 'ID' }}
        </p>
        <el-input
          class="email-input"
          ref="focusOut"
          :placeholder="
            $env !== 'onpremise'
              ? $t('login.emailPlaceholder')
              : $t('onpremise.resetPass.input.placeholder')
          "
          v-model="login.email"
          name="email"
          :class="{ 'input-danger': message }"
          @input="emailRemember(login.email, login.rememberMe)"
          clearable
          v-validate="'required'"
        >
        </el-input>

        <p class="input-title">{{ $t('login.password') }}</p>
        <el-input
          class="password-input"
          :placeholder="$t('login.passwordPlaceholder')"
          v-model="login.password"
          type="password"
          clearable
          name="password"
          :class="{ 'input-danger': message }"
          v-validate="'required'"
          @keyup.enter.native="handleLogin"
        ></el-input>

        <p class="warning-msg danger-color" v-if="errorCode == 2000">
          {{
            $env !== 'onpremise'
              ? this.$t('login.accountError.contents')
              : this.$t('onpremise.login.error')
          }}
        </p>

        <div class="checkbox-wrap">
          <el-checkbox
            v-model="login.rememberMe"
            @change="emailRemember(login.email, login.rememberMe)"
            >{{
              $env !== 'onpremise'
                ? $t('login.remember')
                : $t('onpremise.login.remember')
            }}</el-checkbox
          >
          <el-checkbox
            @change="autoLogin(login.autoLogin)"
            v-model="login.autoLogin"
            >{{ $t('login.autoLogin') }}</el-checkbox
          >
        </div>

        <el-button
          class="next-btn block-btn"
          type="info"
          @click="handleLogin"
          :disabled="isDisable"
          >{{ $t('login.title') }}</el-button
        >
        <div class="find-wrap">
          <router-link
            :to="{ name: 'findTab', params: { findCategory: 'email' } }"
            v-if="$env !== 'onpremise'"
            >{{ $t('login.findAccount') }}</router-link
          >
          <router-link :to="resetPasswordPath">{{
            $t('login.resetPassword')
          }}</router-link>
          <router-link to="/terms" v-if="$env !== 'onpremise'">{{
            $t('login.signUp')
          }}</router-link>
        </div>
      </el-col>
    </el-row>
    <footer-section v-if="$env !== 'onpremise'"></footer-section>
  </div>
</template>

<script>
import loginSlice from 'service/slice/login.slice'
import footerSection from 'layouts/Footer'
export default {
  name: 'login',
  components: {
    footerSection,
  },
  props: {
    auth: Object,
  },
  setup(props, { root }) {
    const LOGIN_SLICE = loginSlice(props, root)
    return {
      ...LOGIN_SLICE,
    }
  },
}
</script>

<style lang="scss" scoped>
.el-button {
  margin-top: 30px;
}

@media (max-width: $mobile) {
  .el-button.next-btn {
    margin-top: 40px;
  }
}
</style>
