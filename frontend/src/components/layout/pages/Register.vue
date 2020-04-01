<template>
	<div class="container">
		<el-row type="flex" justify="center" align="middle" class="row-bg">
			<el-col>
				<h2>회원 정보 등록</h2>
				<p>하나의 계정으로 VIRNECT 정체 제품을 이용할 수 있습니다.</p>

        
				<p class="input-title must-check">계정 이메일</p>
				<el-input
					placeholder="이메일을 입력해 주세요"
          v-model="register.email"
          type="email"
          name="email"
					clearable
          v-validate="'required|email|max:50'"
				>
        </el-input>
        <el-input
					placeholder="인증 번호 6자리를 입력해 주세요"
          v-model="verificationCode"
          type="text"
          name="verificationCode"
					clearable
          maxlength="6"
          v-validate="'required|max:6'"
				>
				</el-input>
        <el-button class="block-btn" type="primary"
        :disabled="!register.email"
        @click="sendEmail()">
          <span>인증 메일 전송</span>
        </el-button>

        <button class="resend-btn">인증 메일 재전송</button>

				<p class="input-title must-check">비밀번호</p>
				<el-input
					placeholder="비밀번호 입력해 주세요"
          v-model="register.password"
					show-password
          name="password"
          v-validate="'required|min:6|max:40'"
          :class="{'input-danger' : errors.has('password')}"
				>
				</el-input>
				<el-input
					placeholder="비밀번호 재입력해 주세요"
          v-model="register.passwordConfirm"
					show-password
          name="passwordConfirm"
          v-validate="'required|min:6|max:40'"
          :class="{'input-danger' : register.password !== register.passwordConfirm}"
				>
				</el-input>
        <p class="restriction-text">8~20자의 영문 대, 소문자, 숫자, 특수문자 중 3가지 이상을 조합하여 입력해 주세요.</p>

        <p class="input-title must-check">이름</p>
				<el-input
          class="firstname-input"
					placeholder="성"
					clearable
          name="firstname"
          v-validate="'required'"
				></el-input>
				<el-input
          class="lastname-input"
					placeholder="이름"
					clearable
          name="lastname"
          v-validate="'required'"
				></el-input>

        
        <p class="input-title must-check">가입 경로</p>
				<el-select v-model="register.registerInfo" placeholder="가입 경로 선택" 
          v-validate="'required'"
          name="registerInfo">
          <el-option
            v-for="item in subscriptionPathLists"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>

				<el-input
					placeholder="가입 경로를 입력해 주세요"
          v-if="register.registerInfo == 9"
          v-model="register.registerInfoETC"
          type="text"
          name="email"
					clearable
          v-validate="'required'"
				>
				</el-input>

        <p class="input-title must-check">서비스 분야</p>
				<el-select v-model="register.serviceInfo" placeholder="서비스 분야 선택"
          v-validate="'required'"
          name="serviceInfo">
          <el-option
            v-for="item in serviceInfoLists"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>

				<el-input
					placeholder="서비스 분야 직접 입력"
          v-if="register.serviceInfo == 12"
          v-model="register.serviceInfoETC"
          type="text"
          name="email"
					clearable
          v-validate="'required'"
				>
				</el-input>

        <el-button class="next-btn block-btn" type="primary" @click="handleRegister()"
					>다음</el-button
				>

			</el-col>
		</el-row>
	</div>
</template>

<script>
  import Register from 'model/register';
  import AuthService from 'service/auth-service';

  export default {
    name: 'register',
    computed: {
      loggedIn() {
        return this.$store.state.auth.status.loggedIn;
      }
    },
    data() {
      return {
        register: {
          email: '',
          password: '',
          passwordConfirm: '',
          familyName: '',
          lastName: '',
          registerInfo: '',
          serviceInfo: '',
          session: ''
        },
        subscriptionPathLists: [
          {
            value: 1,
            label: "검색 사이트"
          }, {
            value: 2,
            label: "전시 및 세미나"
          }, {
            value: 3,
            label: "버넥트 공식 블로그"
          }, {
            value: 4,
            label: "온라인 광고"
          }, {
            value: 5,
            label: "SNS, 커뮤니티"
          }, {
            value: 6,
            label: "언론, 뉴스, 기사, 잡지"
          }, {
            value: 7,
            label: "뉴스레터 및 이메일 홍보"
          }, {
            value: 8,
            label: "지인 추천"
          }, {
            value: 9,
            label: "직접 입력"
          }
        ],
        serviceInfoLists: [
          {
            value: 1,
            label: "에너지/자원"
          }, {
            value: 2,
            label: "EPC"
          }, {
            value: 3,
            label: "자동차/부품"
          }, {
            value: 4,
            label: "항공/철도"
          }, {
            value: 5,
            label: "정유/석유화학"
          }, {
            value: 6,
            label: "물류"
          }, {
            value: 7,
            label: "관공서, 공기업"
          }, {
            value: 8,
            label: "서비스"
          }, {
            value: 9,
            label: "교육/연구"
          }, {
            value: 10,
            label: "소프트웨어 개발 및 공급"
          }, {
            value: 11,
            label: "방위"
          }, {
            value: 12,
            label: "기타"
          }
        ],
        submitted: false,
        successful: false,
        isSendEmail: false,
        isValidEmail: false,
        verificationCode: "",
        message: '',
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
        new Register( this.register.email, this.register.password, this.register.passwordConfirm, this.register.familyName, this.register.lastName, this.register.registerInfo, this.register.serviceInfo, this.register.session )
        console.log(this.register);
        console.log(this.$validator.validate())
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
      sendEmail() {
        alert(this.register.email);
        const email = this.register.email;
        console.log(email);
        const result = AuthService.emailAuth(email);
        console.log(email);
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
