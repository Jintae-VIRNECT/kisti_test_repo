<template>
	<el-row id="footer">
		<el-col>
			<h2>footer</h2>
			<div class="privacy-wrap">
				<a href="https://virnect.com/terms" target="_blank">이용약관</a>
				<a href="https://virnect.com/policy" target="_blank"
					>개인정보처리방침</a
				>
				<!-- <router-link to="/register">도움말</router-link> -->
			</div>
			<p><strong>©VIRNECT CO.</strong>, LTD. All rights reserved.</p>
		</el-col>
	</el-row>
</template>

<script>
export default {}
</script>

<style lang="scss" scoped>
@import '~assets/css/mixin.scss';
#footer {
	position: fixed;
	bottom: 40px;
	left: 0;
	width: 100%;
	color: #7d828b;
	font-size: 13px;
	text-align: center;
	@media (max-width: $tablet) {
		position: relative;
		bottom: auto;
		margin-top: 80px;
	}
	@media (orientation: landscape) {
		position: relative;
		bottom: auto;
		margin-top: 80px;
		font-size: 13px;
	}
	h2 {
		display: none;
	}
	a {
		position: relative;
		padding: 0 11px 0 7px;
		color: #81858c;
		font-weight: normal;
		&:after {
			position: absolute;
			top: 50%;
			right: 0;
			width: 1px;
			height: 12px;
			margin-top: -5px;
			background: #ccc;
			content: '';
		}
		&:last-child::after {
			display: none;
		}
	}
	p {
		margin-top: 10px;
		font-family: 'Roboto';
		opacity: 0.5;
	}
	strong {
		font-weight: bold;
	}
}
</style>
