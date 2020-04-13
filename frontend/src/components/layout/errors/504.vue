<template>
	<section class="container error-pages">
		<img src="~assets/images/common/img-error-disconnect.png" />
		<h2>인터넷 연결을 찾을 수 없습니다.</h2>
		<p class="contents-text">
			현재 VIRNECT 서버에 연결할 수 없습니다. 사용자 네트워크 연결이
			불안정하거나, <br />방화벽 등에 의해 VIRNECT 제품 및 서비스 사용이 차단된
			환경일 수 있습니다.
		</p>
		<p>오류가 반복되면 네트워크 관리자에게 문의해 주세요.</p>

		<dl>
			<dt>Error Code : 504</dt>
			<dd>
				반복된 문제 발생 시 고객센터에 에러 코드와 함께 문의해 주세요.
			</dd>
		</dl>

		<button
			class="next-btn block-btn"
			type="info"
			@click="$router.push({ name: 'login' })"
		>
			새로고침
		</button>
	</section>
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
.block-btn {
	display: block;
	width: 100%;
	max-width: 380px;
	height: 50px;
	margin: 16px auto 0;
	padding: 12px 20px;
	color: #ffffff;
	font-weight: 500;
	font-size: 15px;
	font-family: 'NotoSansKR';
	line-height: 1;
	white-space: nowrap;
	text-align: center;
	background-color: #4d5d74;
	border-radius: 2px;
	cursor: pointer;
	-webkit-transition: 0.1s;
	transition: 0.1s;
	-webkit-appearance: none;
	-moz-user-select: none;
	-webkit-user-select: none;
	-ms-user-select: none;
	& + button {
		margin-top: 12px;
	}
}
dl {
	width: 804px;
	margin: 40px auto 44px;
	padding: 30px 0 34px;
	background: #f2f4f7;
	dt {
		margin-bottom: 4px;
		font-weight: bold;
		font-size: 16px;
	}
	dd {
		font-size: 14px;
	}
}
.contents-text {
	margin-top: 8px;
	color: #0d2a58;
	opacity: 0.9;
}
p {
	margin-top: 12px;
	font-weight: 500;
	font-size: 16px;
}
</style>
