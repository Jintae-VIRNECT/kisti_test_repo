<template>
	<div class="container">
		<el-row type="flex" justify="center" align="middle" class="row-bg">
			<el-col>
				<h2>추가 정보 입력</h2>
				<p>추가 정보를 입력하시면 VIRNECT 제품을 <br>더 유용하게 사용하실 수 있습니다.</p>
        
				<p class="input-title">프로필 이미지</p>
        <article class="profile-image">
          <div class="image-holder">
            <!-- <input type="file" id="profileImage" @change="uploadImage"> -->
            <label for="profileImage">
              <i>
                <img v-if="user.profile" :src="user.profile" class="avatar" @click="profilePopup()">
                <img v-else src="~assets/images/common/ic-user-profile@2x.png" @click="profilePopup()">
              </i>
            </label>
            <i class="camera-ico"><img src="~assets/images/common/ic-camera-alt@2x.png"></i>
          </div>
          <div class="text-wrap">
            <p>프로필 이미지를 등록해 주세요.</p>
            <p>등록하신 프로필 이미지는 다른 사용자들에게 보여집니다.</p>
          </div>
        </article>
        
				<p class="input-title">닉네임</p>
				<el-input
					placeholder="장선영"
          v-model="user.name"
          type="text"
          name="name"
					clearable
          v-validate="'required|min:3|max:20'"
				>
        </el-input>        
        <p class="restriction-text">국문, 영문, 특수문자(&lt;),(&gt;) 제외, 띄어쓰기 포함 20자 이하로 입력해 주세요.</p>

        <dl class="recover-info">
          <dt>계정 복구 정보 입력</dt>
          <dd>계정 분실 시 본인 확인을 위한 정보를 입력해 주세요. </dd>
        </dl>

        <p class="input-title">연락처</p>
				<el-select v-model="user.countryCode" placeholder="+82"
          v-validate="'required'"
          class="countrycode-input"
          name="countryCode">
          <el-option
            v-for="item in countryCodeLists"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>

				<el-input
          class="phonenumber-input"
					placeholder="전화번호를 입력해 주세요"
          v-model="user.phoneNumber"
					clearable
          name="phoneNumber"
          v-validate="'required'"
				></el-input>

				<p class="input-title">복구 이메일 주소</p>
				<el-input
					placeholder="복구 이메일 주소를 입력해 주세요"
          v-model="user.recoveryEmail"
          type="email"
          name="recoveryEmail"
					clearable
          v-validate="'required|email|max:50'"
				>
        </el-input>

        <el-button class="next-btn block-btn" type="primary" 
          @click="handleRegister()"
					>확인</el-button>
        <el-button class="block-btn">나중에 하기</el-button>

			</el-col>
		</el-row>
	</div>
  <!-- <div class="row">
    <div class="card card-container">
      <form id="form" name="form" @submit.prevent="handleRegister">
        <div v-if="!successful">
          <div class="form-group">
            <img
              id="thumbnail"
              v-if="!!form.image"
              :src="register.profile"
              class="profile-img-card"
            />
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
    </div> -->
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
  <!-- </div> -->
</template>

<script>
  import User from 'model/user';
  import AuthService from 'service/auth-service';

  export default {
    name: 'user',
    computed: {
      loggedIn() {
        return this.$store.state.auth.status.loggedIn;
      },
      
    },
    data() {
      return {
        user: {
          profile: '',
          name: '',
          phoneNumber: '',
          recoveryEmail: '',
        },
        countryCodeLists: [
          {
            value: 1,
            label: '+82'
          }, {
            value: 2,
            label: '+82'
          }
        ],
        submitted: false,
        successful: false,
        isSendEmail: false,
        isValidEmail: false,
        isShow: false,
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
      profilePopup() {
        this.$alert('프로필 이미지가 전체 VIRNECT 사용자에게 보여집니다. ', '프로필 이미지 설정', {
          confirmButtonText: '확인',
          cancelButtonText: 'Cancel'
        }); 
      },
      handleRegister() {
        this.message = '';
        this.submitted = true;
        new Register( this.register.email, this.register.password, this.register.passwordConfirm, this.register.familyName, this.register.lastName, this.register.registerInfo, this.register.serviceInfo, this.register.session )
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
              // this.$router.push('/')
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
            this.user.profile = files[0];
            this.user.profile = imageData;
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

<style lang="scss" scoped>

  .el-button.next-btn {
    margin-top: 60px;
  }
</style>
