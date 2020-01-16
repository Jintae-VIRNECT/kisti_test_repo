<template lang="pug">

  el-card
    .profile-card
      .profile-card--profile.label 프로필
      .profile-card--top
        .profile-card--top-left
          .profile-card--workspace-name {{profileData.workspaceName}}
          .profile-card--email {{profileData.email}}
          .profile-card--role.capitalize 
            span {{profileData.role | upperCases}}
        .profile-card--top-right
          .img-wrapper
            img(:src='importImage(profileData.image)' )
      .profile-card--bottom
        .label 공정관리
        .percent-label 진행 중인 업무 / 할당된 업무
        el-progress(:percentage='setWorkPercent(profileData)' :show-text="false")
        .stats.text-right
          span.done {{profileData.done}}&nbsp;
          span /{{profileData.all}}
        router-link.direct-link(to="/process") 바로가기
        .profile-card--bottom-tool
          router-link(to="/process") 
            img(src="~@/assets/image/ic-process-dark.svg")
            span 공정
          router-link(to="/issue") 
            img(src="~@/assets/image/ic-issue-dark.svg")
            span 이슈
          router-link(to="/contents") 
            img(src="~@/assets/image/ic-content-dark.svg")
            span 콘텐츠

</template>
<style lang="scss">
.profile-card {
	padding: 22px 24px;

	.profile-card--top {
		padding-bottom: 32px;
		margin-bottom: 8px;
		border-bottom: 1px solid #e6e9ee;
		&-right {
			width: 49%;
			display: inline-block;
			vertical-align: middle;
		}
		&-left {
			width: 49%;
			display: inline-block;
			vertical-align: middle;
		}
	}
	&--profile {
		margin-bottom: 10px;
	}
	&--workspace-name {
		font-size: 18px;
		font-weight: 500;
		line-height: 1.33;
		color: #0d2a58;
		margin-bottom: 2px;
	}
	&--email {
		font-size: 13px;
		font-weight: 500;
		line-height: 1.54;
		color: #6d798b;
		margin-bottom: 12px;
	}
	&--role {
		display: inline-block;
		font-size: 11px;
		padding: 2px 8px;
		font-weight: 500;
		line-height: 1.54;
		color: #186ae2;
		border-radius: 11px;
		border: solid 1px #186ae2;
	}
	.img-wrapper {
		background-color: #e2e7ed;
		border-radius: 30%;
		width: 80px;
		height: 80px;
		float: right;
		img {
			width: 100%;
			padding: 5px;
		}
	}
	.stats {
		margin-top: 4px;
		margin-bottom: 12px;
	}
	.direct-link {
		margin-bottom: 10px;
		font-size: 12px;
		font-weight: 500;
		color: #6d798b;
	}
	.label {
		font-size: 12px;
		color: #6d798b;
	}
	.percent-label {
		font-size: 13px;
		line-height: 1.54;
		color: #0d2a58;
		margin-bottom: 8px;
	}
	.profile-card--bottom {
		> .label {
			margin-bottom: 10px;
		}
		.done {
			color: #186ae2;
		}
		&-tool {
			a {
				padding: 2px 8px 2px 6px;
				border-radius: 4px;
				background-color: #f2f5f9;
				margin-right: 10px;
			}
			img,
			span {
				vertical-align: middle;
			}
			span {
				font-size: 13px;
				color: #0d2a58;
			}
			img {
				margin-right: 4px;
				width: 24px;
				height: 24px;
			}
		}
	}
}
</style>
<script>
export default {
	props: ['profileData'],
	methods: {
		importImage() {
			return require(`@/assets/image/ic-user-profile.svg`)
			// return require(src
			// 	? `@/assets/image/admin/${src}`
			// 	: `@/assets/image/ic-user-profile.svg`)
		},
		getImgUrl(src) {
			var images = require.context('@/', false, /\.png$/)
			return images(src + '.png')
		},
		setWorkPercent({ done, all }) {
			return (done / all) * 100
		},
	},
	filters: {
		upperCases(str) {
			return str.toUpperCase()
		},
	},
}
</script>
