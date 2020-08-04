<template>
	<div class="container">
		<el-row type="flex" justify="center" align="middle">
			<el-col>
				<h2>{{ $t('qrLoginCenter.title') }}</h2>
				<p v-html="$t('qrLoginCenter.pageInfo')"></p>

				<div class="qr-login-body">
					<p>
						[{{ $t('qrLoginCenter.loginRole')
						}}<i v-if="isExpire"> {{ $t('qrLoginCenter.expire') }}</i
						>]
					</p>
					<div class="qr-image-box" :class="{ 'code-expire': isExpire }">
						<el-image
							v-if="qrImage"
							:src="qrImage"
							:preview-src-list="[qrImage]"
							alt="qrcode image"
							class="qrcode--image__inner"
						/>
						<p v-if="isExpire">{{ $t('qrLoginCenter.renewalCode') }}</p>
					</div>
					<p>{{ $t('qrLoginCenter.effectiveTime') }}</p>
					<p class="qr-expire-count">{{ showTime }}</p>
					<el-button
						class="next-btn block-btn"
						@click="reset()"
						type="primary"
						>{{ $t('qrLoginCenter.resendCode') }}</el-button
					>
				</div>
				<div class="howto-qr-login">
					<p class="title">{{ $t('qrLoginCenter.howToLogin') }}</p>
					<div>
						<ol>
							<li
								v-for="(list, idx) of $t('qrLoginCenter.centerHowTo')"
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
@import '~element/image.css';

p {
	font-weight: 500;
	font-family: 'NotoSansKR', 'Noto Sans';
}
.container {
	text-align: center;
}
</style>
