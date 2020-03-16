<template>
  <div class="row">
    <div class="card card-container">
      <form id="form" name="form" @submit.prevent="handleRegister">
        <div v-if="!successful">
          <div class="form-group">
            <label for="username">이름</label>
            <input
              id="username"
              type="text"
              class="form-control"
              name="name"
              v-model="register.name"
              v-validate="'required|min:3|max:20'"
            />
            <div
              class="alert-danger"
              v-if="submitted && errors.has('username')"
            >{{errors.first('username')}}
            </div>
          </div>
          <div class="form-group">
            <label for="email">이메일</label>
            <div class="form-inline">
              <input
                id="email"
                type="email"
                class="form-control col-sm-8"
                name="email"
                v-model="register.email"
                v-validate="'required|email|max:50'"
              />
              <b-button v-b-modal.email-verify class="form-control col-sm-4">인증</b-button>
            </div>
            <div
              class="alert-danger"
              v-if="submitted && errors.has('email')"
            >{{errors.first('email')}}
            </div>
          </div>
          <div class="form-group">
            <label for="password">비밀번호</label>
            <input
              id="password"
              type="password"
              class="form-control"
              name="password"
              v-model="register.password"
              v-validate="'required|min:6|max:40'"
            />
            <div
              class="alert-danger"
              v-if="submitted && errors.has('password')"
            >{{errors.first('password')}}
            </div>
          </div>
          <div class="form-group">
            <img
              id="thumbnail"
              v-if="!!form.image"
              :src="register.profile"
              class="profile-img-card"
            />
            <!--            <img v-if="!!form.image" id="thumbnail" :src="register.profile">-->
            <div class="form-inline">
              <label for="profile" class="fom">프로필</label>
              <input
                id="profile"
                type="file"
                class="form-control-file"
                name="profile"
                accept="image/gif,image/jpeg,image/png"
                @change="uploadImage($event)"/>
            </div>
          </div>
          <div class="form-group">
            <label for="phoneNumber">전화번호</label>
            <input
              id="phoneNumber"
              type="tel"
              class="form-control"
              name="phoneNumber"
              v-model="register.phoneNumber"
            >
          </div>
          <div class="form-group">
            <label for="recoveryEmail">복구 이메일</label>
            <input
              id="recoveryEmail"
              type="email"
              class="form-control"
              name="recoveryEmail"
              v-model="register.recoveryEmail"
            >
          </div>
          <div class="form-group">
            <label for="birth">생년월일</label>
            <input
              id="birth"
              class="form-control"
              type="text"
              name="birth"
              v-model="register.birth"
            >
          </div>
          <div class="form-group">
            <label for="registerInfo">가입 경로</label>
            <input
              id="registerInfo"
              type="text"
              class="form-control"
              name="registerInfo"
              v-model="register.registerInfo"
              v-validate="'required'"
            >
            <div
              class="alert-danger"
              v-if="submitted && errors.has('registerInfo')"
            >{{errors.first('registerInfo')}}
            </div>
          </div>
          <div class="form-group">
            <label for="serviceInfo">서비스 분야</label>
            <input
              id="serviceInfo"
              type="text"
              class="form-control"
              name="serviceInfo"
              v-model="register.serviceInfo"
              v-validate="'required'"
            >
            <div
              class="alert-danger"
              v-if="submitted && errors.has('serviceInfo')"
            >{{errors.first('serviceInfo')}}
            </div>
          </div>

          <div class="form-group">
            <button type="submit" class="btn btn-primary btn-block">회원가입</button>
            <div class="d-flex justify-content-center links">
              <a href="/" class="ml-2">로그인</a>
            </div>
          </div>
        </div>
      </form>
      <div
        class="alert"
        :class="successful ? 'alert-success' : 'alert-danger'"
        v-if="message">{{message}}
      </div>
    </div>
    <div>
      <b-modal id="email-verify" centered title="이메일 인증">
        <div>
          <b-form-input v-model="verificationCode" placeholder="이메일로 전송된 인증코드 6자리를 입력하세요"></b-form-input>
          <div class="mt-2">이메일로 전송된 인증코드 6자리를 입력하세요</div>
        </div>
      </b-modal>
    </div>
    <!--    &lt;!&ndash; #2 : Modal Window &ndash;&gt;-->
    <!--    <div class="modal" v-if="isShow">-->
    <!--      <div class="modal-body">-->
    <!--        <div class="form-control">-->
    <!--          <label for="verificationCode">인증코드</label>-->
    <!--          <input-->
    <!--            id="verificationCode"-->
    <!--            name="verificationCode"-->
    <!--            type="number"-->
    <!--            maxlength="6"-->
    <!--          >-->
    <!--        </div>-->
    <!--      </div>-->
    <!--      <button @click="checkVerificationCode()" type="button">-->
    <!--        확인-->
    <!--      </button>-->
    <!--    </div>-->
  </div>
</template>

<script>
  import Register from '../model/register';
  import AuthService from '../service/auth-service';

  export default {
    name: 'register',
    computed: {
      loggedIn() {
        return this.$store.state.auth.status.loggedIn;
      }
    },
    data() {
      return {
        register: new Register('', '', '', '', '', '', '', '', '', ''),
        submitted: false,
        successful: false,
        isSendEmail: false,
        isValidEmail: false,
        isShow: false,
        verificationCode: 0,
        message: '',
        form: {
          image: "",
        }
      };
    },
    mounted() {
      if (this.loggedIn) {
        this.$router.push('/');
      }
    },
    methods: {
      handleRegister() {
        this.message = '';
        this.submitted = true;
        console.log(this.register);
        this.$validator.validate().then(valid => {
          if (valid) {
            this.$store.dispatch('auth/register', this.register).then(
              data => {
                this.message = data.message;
                this.successful = true;
              },
              error => {
                this.message = error.message;
                this.successful = false;
              }
            ).then(
              this.$router.push('/')
            )
          }
        })
      },
      validImage(event) {
        const files = event.target.files;
        console.log(files);
        return new Promise((resolve, reject) => {
          if (files.length > 0) {
            if (['image/gif', 'image/jpeg', 'image/jpg', 'image/png'].indexOf(files[0].type) < 0) {
              reject('This image is unavailable.');
              return;
            }
            if (files[0].size > (2 * 1024 * 1024)) {
              reject('This image size is unavailable.');
              return;
            }
            this.form.image = null;

            const oReader = new FileReader();
            oReader.onload = (e) => {
              const imageData = e.target.result;
              const oImg = new Image();
              oImg.onload = (_event) => {
                resolve(imageData);
                _event.target.remove();
              };
              oImg.onerror = (_event) => {
                //이미지 아닐 시 처리.
                reject('This image is unavailable.');
              };
              oImg.src = imageData;
            };
            oReader.readAsDataURL(files[0]);
          }
        })
      },
      uploadImage(event) {
        const files = event.target.files;
        this.validImage(event)
          .then((imageData) => {
            console.log(imageData);
            this.register.profile = files[0];
            this.register.profile = imageData;
            this.form.image = imageData;
          })
          .catch((error) => {
            console.log(error);
          })
      },
      sendEmail() {
        alert(this.register.email);
        const email = this.register.email;
        console.log(email);
        const result = AuthService.emailAuth(email);
        console.log(email);
        this.isShow = true;
      },
      checkVerificationCode() {

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
    background-image: url("//ssl.gstatic.com/accounts/ui/avatar_2x.png");
  }
</style>
