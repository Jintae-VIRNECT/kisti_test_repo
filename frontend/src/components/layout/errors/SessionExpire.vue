<template>
	<div class="container">
		<el-row type="flex" justify="center" align="middle" class="error-pages">
			<el-col>
				<img src="~assets/images/common/img-error-timeout.png" />
				<h2>세션 만료 안내</h2>
				<p>
					사용자의 활동이 없어 계정 보안을 위해 이용 중인 서비스 세션이
					만료되었습니다.
				</p>

				<dl>
					<dt>Error Code : 404</dt>
					<dd>
						반복된 문제 발생 시 고객센터에 에러 코드와 함께 문의해 주세요.
					</dd>
				</dl>

				<el-button
					class="next-btn block-btn"
					type="info"
					@click="$router.push({ name: 'login' })"
					>새로고침</el-button
				>
			</el-col>
		</el-row>
	</div>
</template>

<script>
export default {
	data() {
		return {}
	},
	methods: {},
}
</script>

<style lang="scss" scoped>
.error-pages {
	text-align: center;
}
.el-button.block-btn {
	font-size: 15px;
	font-family: 'NotoSansKR';
	& + .block-btn {
		margin-top: 12px;
	}
}
dl {
	width: 804px;
	margin: 40px auto 44px;
	padding: 30px 0 34px;
	background: #f5f7fa;
	dt {
		margin-bottom: 4px;
		font-weight: bold;
		font-size: 16px;
	}
	dd {
		font-size: 14px;
	}
}
</style>
