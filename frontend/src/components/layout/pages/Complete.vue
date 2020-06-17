<template>
	<div class="container">
		<el-row type="flex" justify="center" align="middle" class="row-bg">
			<el-col>
				<h2>{{ $t('complete.title') }}</h2>
				<p>{{ $t('complete.welcome') }}</p>

				<el-button
					class="next-btn block-btn"
					@click="trialCoupon"
					type="primary"
					>{{ $t('complete.trial') }}</el-button
				>
				<el-button class="next-btn block-btn" type="info">{{
					$t('complete.payment')
				}}</el-button>

				<p class="divide" v-html="$t('complete.info')"></p>
				<el-button class="next-btn block-btn" type="info" @click="goWorkstation"
					>VIRNECT Workstation
				</el-button>
			</el-col>
		</el-row>
	</div>
</template>
<script>
import urls from 'WC-Modules/javascript/api/virnectPlatform/urls'
export default {
	data() {
		return {
			couponUrl: urls.www[process.env.TARGET_ENV],
			workstationUrl: urls.workstation[process.env.TARGET_ENV],
		}
	},
	methods: {
		goWorkstation() {
			location.replace(this.workstationUrl)
			// window.open(this.workstationUrl)
			// console.log(this.workstationUrl)
		},
		trialCoupon() {
			location.replace(`${this.couponUrl}/coupon`)
			// console.log(this.couponUrl)
		},
	},
}
</script>

<style lang="scss" scoped>
.el-button.block-btn {
	font-size: 15px;
	font-family: 'NotoSansKR';
	& + .block-btn {
		margin-top: 12px;
	}
}
.divide {
	margin-top: 40px;
	padding-top: 40px;
	color: #0d2a58;
	font-weight: 500;
	font-size: 14px;
	border-top: 1px solid #e6e9ee;
}
</style>
