<template>
  <div class="col-md-12">
    <div class="card card-container">
      <img
        id="profile-img"
        src="//ssl.gstatic.com/accounts/ui/avatar_2x.png"
        class="profile-img-card"
      />
      <form name="form" @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="email">이메일</label>
          <input
            id="email"
            type="text"
            class="form-control"
            name="email"
            v-model="login.email"
            v-validate="'required'"
          />
          <div
            class="alert alert-danger"
            role="alert"
            v-if="errors.has('email')"
          >이메일을 입력해주세요
          </div>
        </div>
        <div class="form-group">
          <label for="password">비밀번호</label>
          <input
            id="password"
            type="password"
            class="form-control"
            name="password"
            v-model="login.password"
            v-validate="'required'"
          />
          <div
            class="alert alert-danger"
            role="alert"
            v-if="errors.has('password')"
          >비밀번호를 입력해주세요
          </div>
        </div>
        <div class="form-group">
          <button class="btn btn-primary btn-block" :disabled="loading">
            <span class="spinner-border spinner-border-sm" v-show="loading"></span>
            <span>로그인</span>
          </button>
        </div>
        <div class="form-group">
          <div class="custom-control custom-checkbox">
            <input id="remember-me" type="checkbox" class="custom-control-input" name="rememberMe"
                   v-model="login.rememberMe">
            <label for="remember-me" class="custom-control-label">자동 로그인</label>
          </div>
        </div>
        <div class="form-group">
          <div class="alert alert-danger" role="alert" v-if="message">{{message}}</div>
        </div>
      </form>
      <div class="mt-4">
        <div class="d-flex justify-content-center links">
          <a href="/register" class="ml-2">회원가입</a>
        </div>
        <div class="d-flex justify-content-center links">
          <a href="/find">비밀번호 찾기</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import Login from '../model/login';

  export default {
    name: 'login',
    computed: {
      loggedIn() {
        return this.$store.state.auth.status.loggedIn;
      }
    },
    data() {
      return {
        login: new Login('smic1', 'smic1234'),
        loading: false,
        message: ''
      };
    },
    mounted() {
      if (this.loggedIn) {
        this.$router.push('/profile');
      }
    },
    methods: {
      handleLogin() {
        this.loading = true;
        this.$validator.validateAll();
        if (this.errors.any()) {
          this.loading = false;
          return;
        }
        if (this.login.email && this.login.password) {
          this.$store.dispatch('auth/login', this.login).then(
            () => {
              this.$router.push('/profile');
            },
            error => {
              this.loading = false;
              this.message = error.message;
            }
          );
        }
      }
    }
  };
</script>

<style scoped>
  label {
    display: block;
    margin-top: 10px;
  }

  .card-container.card {
    max-width: 350px !important;
    padding: 40px 40px;
  }

  .card {
    background-color: #f7f7f7;
    padding: 20px 25px 30px;
    margin: 0 auto 25px;
    margin-top: 50px;
    -moz-border-radius: 2px;
    -webkit-border-radius: 2px;
    border-radius: 2px;
    -moz-box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
    -webkit-box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
    box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
  }

  .profile-img-card {
    width: 96px;
    height: 96px;
    margin: 0 auto 10px;
    display: block;
    -moz-border-radius: 50%;
    -webkit-border-radius: 50%;
    border-radius: 50%;
  }
</style>
