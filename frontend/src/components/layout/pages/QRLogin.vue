<template>
	<div class="container">
		<el-row type="flex" justify="center" align="middle" class="row-bg">
			<el-col>
				<h2>QR 로그인</h2>
				<p>
					QR 로그인 센터에서 발급한 로그인용 QR을 <br />인식하여 로그인합니다.
				</p>

				d
			</el-col>
		</el-row>
	</div>
</template>

<script>
export default {
	data() {
		return {
			allTerms: null,
			serviceAgree: false,
			privacyAgree: false,
			marketingAgree: false,
		}
	},
	methods: {
		// allTerms() {
		//   console.log(allTerms)
		//   this.serviceAgree = !this.serviceAgree
		//   this.privacyAgree = !this.privacyAgree
		// }
	},
}
</script>

<style lang="scss" scoped>
p {
	font-family: 'NotoSansKR', 'Noto Sans';
}
</style>
