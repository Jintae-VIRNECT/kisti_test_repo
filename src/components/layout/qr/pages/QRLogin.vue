<template>
	<div class="container">
		<TheHeader :showSection="showSection">
			<template slot="subTitle">{{ $t('qrLogin.title') }}</template>
		</TheHeader>
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
import Auth from 'api/virnectPlatformAuth'
import TheHeader from 'WC-Modules/vue/components/header/TheHeader'
import { QrcodeStream } from 'vue-qrcode-reader'

export default {
	components: {
		TheHeader,
		QrcodeStream,
	},
	data() {
		return {
			myInfo: {},
			showSection: {
				login: true,
				profile: false,
			},
			result: '',
			error: '',
		}
	},
	methods: {
		onDecode(result) {
			this.result = result
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
	async mounted() {
		try {
			await Auth.init()
			if (Auth.isLogin) {
				this.myInfo = Auth.myInfo
				this.showSection.login = false
				this.showSection.link = true
				this.showSection.profile = true
			} else throw 'error'
		} catch (e) {
			this.showSection.login = true
			this.showSection.profile = false
			this.showSection.link = false
			location.replace(`${window.urls['console']}/?continue=${location.href}`)
		}
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
</style>
