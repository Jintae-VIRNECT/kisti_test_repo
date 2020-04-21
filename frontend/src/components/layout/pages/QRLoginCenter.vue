<template>
	<div class="container">
		<el-row type="flex" justify="center" align="middle">
			<el-col>
				<h2>QR 로그인 센터</h2>
				<p>
					QR 로그인 센터에서 발급한 로그인용 QR을 <br />인식하여 로그인합니다.
				</p>

				<div class="qr-login-body">
					<p>[로그인용 QR코드]</p>
					<p>유효시간</p>
					<p></p>
					<el-button class="next-btn block-btn" type="primary"
						>QR 코드 갱신</el-button
					>
				</div>
				<div class="howto-qr-login">
					<p class="title">QR 로그인 방법</p>
					<div>
						<ol>
							<li v-for="(list, idx) of $t('qrLogin.howTo')" :key="idx">
								<p>
									<span>{{ idx + 1 }}.</span>{{ list }}
								</p>
							</li>
						</ol>
						<p>* QR 로그인 기능은 지원 가능 기기에서만 제공됩니다.</p>
					</div>
				</div>
			</el-col>
		</el-row>
	</div>
</template>

<script>
import Auth from 'api/virnectPlatformAuth'
export default {
	data() {
		return {
			allTerms: null,
			serviceAgree: false,
			privacyAgree: false,
			marketingAgree: false,
			auth: Auth,
		}
	},
	methods: {
		async checkToken() {
			const user = await this.auth.init({
				API_GATEWAY_URL: process.env.API_GATEWAY_URL,
				LOGIN_SITE_URL: process.env.LOGIN_SITE_URL,
			})
			// console.log(user)
			if (user.isLogin) {
				try {
					const userInfo = user.myInfo
					this.showSection.login = user.isLogin
					this.showSection.profile = true
					this.showSection.link = true
					this.profileInfo = userInfo
					console.log(this.profileInfo)
					// console.log('왜안돼')
				} catch (e) {
					console.error(e)
				}
			} else {
				this.showSection.login = false
				this.showSection.profile = false
				this.showSection.link = false
			}
		},
	},
	mounted() {
		this.checkToken()
	},
}
</script>

<style lang="scss" scoped>
p {
	font-weight: 500;
	font-family: 'NotoSansKR', 'Noto Sans';
}
.container {
	text-align: center;
}
.qr-login-body {
	width: 804px;
	margin: 20px auto 0;
	padding-top: 44px;
	border-top: 2px solid #e6e9ee;
}
.howto-qr-login {
	position: relative;
	width: 804px;
	margin: 44px auto;
	padding: 28px 0 30px;
	text-align: left;
	background: #f2f4f7;
	.title {
		position: absolute;
		top: 28px;
		left: 40px;
		font-size: 16px;
	}
	> div {
		padding-left: 278px;
	}
	ol {
		margin-bottom: 12px;
		line-height: 1.85;
		p {
			color: #566173;
		}
	}
	p {
		color: #24282f;
		font-size: 13px;
	}
}
</style>
