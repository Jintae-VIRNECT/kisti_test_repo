<template>
	<section class="container error-pages">
		<img src="~WC-Modules/assets/images/errors/img-error-server.png" />
		<h2>죄송합니다. <br />서비스 이용이 원활하지 않습니다.</h2>
		<p>VIRNECT 회원이 되신 것을 환영합니다.</p>

		<dl>
			<dt>Error Code : 404</dt>
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
	props: {
		errorCode: Object,
	},
}
</script>

<style lang="scss" scoped>
h2 {
	font-weight: 500;
	font-size: 28px;
	font-family: 'NotoSansKR';

	+ p {
		margin-top: 8px;
		font-weight: 500;
		font-size: 15px;
	}
}
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
</style>
