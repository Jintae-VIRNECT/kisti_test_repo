<template>
	<div class="container">
		<el-row type="flex" justify="center" align="middle">
			<el-col>
				<h2>QR 로그인 센터</h2>
				<p>
					QR 로그인 센터에서 발급한 로그인용 QR을 <br />인식하여 로그인합니다.
				</p>

				<div class="qr-login-body">
					<p>[로그인용 QR코드<i v-if="isExpire"> 만료</i>]</p>
					<div class="qr-image-box" :class="{ 'code-expire': isExpire }">
						<img
							v-if="qrImage"
							:src="qrImage"
							alt="qrcode image"
							class="qrcode--image__inner"
						/>
						<p v-if="isExpire">QR코드를 갱신해 주세요.</p>
					</div>
					<p>유효시간</p>
					<p class="qr-expire-count">{{ showTime }}</p>
					<el-button class="next-btn block-btn" @click="reset()" type="primary"
						>QR 코드 갱신</el-button
					>
				</div>
				<div class="howto-qr-login">
					<p class="title">QR 로그인 방법</p>
					<div>
						<ol>
							<li v-for="(list, idx) of $t('qrLogin.centerHowTo')" :key="idx">
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
import urls from 'WC-Modules/javascript/api/virnectPlatform/urls'
import AuthService from 'service/auth-service'
import dayjs from 'dayjs'
import utc from 'dayjs/plugin/utc'
import duration from 'dayjs/plugin/duration'
export default {
	props: {
		myInfo: Object,
	},
	data() {
		return {
			runnerID: null,
			deadline: dayjs()
				.add(3, 'minute')
				.unix(),
			remainTime: 0,
			loginUrl: urls.console[process.env.TARGET_ENV],
			curruntUrl: location.href,
			otpInfo: null,
			qrImage: null,
			isExpire: false,
		}
	},
	computed: {
		showTime() {
			let minute = parseInt(this.remainTime / 60)
			let second = parseInt(this.remainTime % 60)
			if (minute < 10) {
				minute = '0' + minute
			}
			if (second < 10) {
				second = '0' + second
			}
			return `${minute}:${second}`
		},
	},
	methods: {
		async reset() {
			this.otpInfo = {
				email: this.$props.myInfo.email,
				userId: this.$props.myInfo.uuid,
			}
			try {
				let otp = await AuthService.qrOtp(this.otpInfo)
				if (otp.code == 200) {
					this.timeRunner()
					this.deadline = dayjs()
						.add(3, 'minute')
						.unix()
					this.remainTime = parseInt(this.deadline - dayjs().unix())
					this.qrImage = `data:image/png;base64,${otp.data.qrCode}`
				} else {
					throw otp.data
				}
			} catch (e) {
				location.replace(`${this.loginUrl}/?continue=${this.curruntUrl}`)
			}
		},
		timeRunner() {
			clearInterval(this.runnerID)
			dayjs.extend(duration)
			dayjs.extend(utc)
			this.isExpire = false
			this.runnerID = setInterval(() => {
				const diff = this.deadline - dayjs().unix()
				this.remainTime = parseInt(dayjs.duration({ second: diff }).$ms / 1000)
				if (this.remainTime <= 0) {
					// console.log('만료')
					this.isExpire = true
					clearInterval(this.runnerID)
				}
			}, 1000)
		},
	},
	watch: {
		async myInfo() {
			this.reset()
		},
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
.qr-image-box {
	position: relative;
	display: inline-block;
	width: 240px;
	height: 240px;
	margin: 10px 0 26px;
	// padding: 16px;
	border: 1px solid #e2e7ed;
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
