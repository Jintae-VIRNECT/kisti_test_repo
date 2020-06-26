<template>
	<div class="container">
		<el-row type="flex" justify="center" align="middle">
			<el-col>
				<h2>{{ $t('qrLogin.title') }}</h2>
				<p v-html="$t('qrLogin.pageInfo')"></p>

				<div class="qr-login-body">
					<p>{{ $t('qrLogin.manual') }}</p>
					<div class="qr-image-box">
						<qrcode-stream @decode="onDecode" @init="onInit" />
					</div>
				</div>
				<div class="howto-qr-login">
					<p class="title">{{ $t('qrLoginCenter.howToLogin') }}</p>
					<div>
						<ol>
							<li
								v-for="(list, idx) of $t('qrLoginCenter.loginHowTo')"
								:key="idx"
							>
								<p>
									<span>{{ idx + 1 }}. </span>{{ list }}
								</p>
							</li>
						</ol>
						<p>{{ $t('qrLoginCenter.LoginInfo') }}</p>
					</div>
				</div>
			</el-col>
		</el-row>
	</div>
</template>

<script>
import { QrcodeStream } from 'vue-qrcode-reader'
// import AuthService from 'service/auth-service'

export default {
	// props: {
	// 	myInfo: Object,
	// },
	components: {
		QrcodeStream,
	},
	data() {
		return {
			result: '',
			error: '',
		}
	},
	methods: {
		onDecode(result) {
			this.result = result
			console.log(this.result)
		},
		async onInit(promise) {
			try {
				await promise
			} catch (error) {
				if (error.name === 'NotAllowedError') {
					this.error = 'ERROR: you need to grant camera access permisson'
				} else if (error.name === 'NotFoundError') {
					this.error = 'ERROR: no camera on this device'
				} else if (error.name === 'NotSupportedError') {
					this.error = 'ERROR: secure context required (HTTPS, localhost)'
				} else if (error.name === 'NotReadableError') {
					this.error = 'ERROR: is the camera already in use?'
				} else if (error.name === 'OverconstrainedError') {
					this.error = 'ERROR: installed cameras are not suitable'
				} else if (error.name === 'StreamApiNotSupportedError') {
					this.error = 'ERROR: Stream API is not supported in this browser'
				}
			}
		},
	},
	watch: {
		// async myInfo() {
		// 	this.reset()
		// },
	},
}
</script>

<style lang="scss" scoped>
@import '~assets/css/mixin.scss';
p {
	font-weight: 500;
	font-family: 'NotoSansKR', 'Noto Sans';
}
.container {
	text-align: center;
}
.qr-login-body {
	@include area-flex(804px);
	// width: 804px;
	margin: 20px auto 0;
	padding-top: 44px;
	border-top: 2px solid #e6e9ee;
}
.qr-image-box {
	position: relative;
	display: inline-block;
	width: 300px;
	height: 300px;
	margin: 24px 0 28px;
	// padding: 16px;
	// border: 1px solid #e2e7ed;
	&.code-expire {
		p {
			position: absolute;
			top: 50%;
			left: 0;
			width: 100%;
			color: #db1717;
			font-weight: 500;
			font-size: 16px;
			transform: translateY(-50%);
		}
		img {
			opacity: 0.1;
		}
	}
}
.qr-expire-count {
	color: #1655bf;
	font-weight: 500;
	font-size: 28px;
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
