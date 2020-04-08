<template>
	<section class="login-modlue">
		<ul class="login-wrap" v-if="status == 'visitor' && false">
			<li class="login-btn"><a href="#">Log in</a></li>
			<li class="singup-btn"><a href="#">Sign up</a></li>
		</ul>

		<input
			type="text"
			placeholder="서비스, 메뉴 및 도움말 검색"
			v-model="search"
			v-if="status == 'workstation'"
		/>

		<a href="#">플랜구매</a>

		<button
			class="link-btn status-btn"
			@click="langBox = !langBox"
			v-if="status == 'workstation'"
		>
			<img src="~assets/images/common/ic-help-outline@2x.png" />
			<ul class="hover-box" v-if="langBox">
				<li class="active"><button>Kor</button></li>
				<li><button>Eng</button></li>
			</ul>
		</button>

		<button
			class="link-btn status-btn"
			@click="langBox = !langBox"
			v-if="status == 'workstation'"
		>
			<img src="~assets/images/common/ic-notifications@2x.png" />
			<ul class="hover-box" v-if="langBox">
				<li class="active"><button>Kor</button></li>
				<li><button>Eng</button></li>
			</ul>
		</button>

		<button
			class="language-btn status-btn"
			@click="langBox = !langBox"
			v-if="status == 'visitor'"
		>
			<img src="~assets/images/common/ic-language@2x.png" />
			<ul class="hover-box" v-if="langBox">
				<li class="active"><button>Kor</button></li>
				<li><button>Eng</button></li>
			</ul>
		</button>

		<button
			class="link-btn status-btn"
			@click="linkControl()"
			v-if="status == 'login' || ('workstation' && false)"
		>
			<img src="~assets/images/common/ic-apps@2x.png" />
			<div class="hover-box" v-if="linkBox">
				<p>바로가기</p>
				<ul class="lang-box">
					<li class="icon"><button>Remote</button></li>
					<li><button>QR 로그인 센터</button></li>
					<li><button>Workstation</button></li>
				</ul>
			</div>
		</button>
		<button
			class="status-btn thumbnail-btn"
			v-if="status == 'login' || ('workstation' && false)"
		>
			<img
				src="~assets/images/common/ico-kakao-normal@2x.png"
				@click="thumbBoxEvent()"
			/>
			<div class="hover-box" v-if="thumbBox">
				<div class="thumbnail-head">
					<div class="img-wrap">
						<div
							class="profile-img"
							style="background-image: url('../images/common/ico-kakao-normal@2x.png')"
						>
							<button class="img-btn"></button>
						</div>
					</div>
					<div class="info-wrap">
						<p class="name-text">버넥트</p>
						<p class="email-text">virnect@virnect.com</p>
						<button>VIRNECT 계정 관리</button>
					</div>
				</div>
				<div class="thumbnail-body">
					<button>로그아웃</button>
				</div>
				<div class="thumbnail-foot">
					<router-link to="/policy">개인정보처리방침</router-link>
					<router-link to="/terms">서비스 약관</router-link>
					<button>
						<img src="~assets/images/common/ic-language@2x.png" />한국어
					</button>
				</div>
			</div>
		</button>
	</section>
</template>

<script>
export default {
	props: {
		status: String,
	},
	data() {
		return {
			langBox: false,
			linkBox: false,
			thumbBox: false,
			search: '',
		}
	},
	methods: {
		linkControl() {
			this.linkBox = !this.linkBox
			this.thumbBox = false
		},
		thumbBoxEvent() {
			this.thumbBox = !this.thumbBox
			this.linkBox = false
		},
	},
}
</script>

<style lang="scss" scoped>
@import '~assets/css/modules/status.scss';
</style>
