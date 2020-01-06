<template lang="pug">
  el-card.box-card
    .clearfix(slot='header')
      span 프로필
    div
      img(style='width: 100px; height: 100px' :src='importImage(profileData.image)' )
      el-button(type='primary' plain size="mini") Primary
      p {{profileData.username}}
      p XX팀원 {{profileData.username}}입니다.
    div
      p 공정관리
      hr
      p 진행 중인 업무 / 할당된 업무
      el-progress(:percentage='setWorkPercent(profileData)' :show-text="false")
      el-button(type="primary" size='mini') 공정 관리
      el-button(type="info" size='mini') 콘텐츠 목록

</template>

<script>
// import a from '@/assets/image/admin/ic-user.svg.png'

export default {
	props: ['profileData'],
	methods: {
		importImage(src) {
			return require(`@/assets/image/admin/${src}`)
		},
		getImgUrl(src) {
			var images = require.context('@/', false, /\.png$/)
			return images(src + '.png')
		},
		setWorkPercent({ done, all }) {
			return (done / all) * 100
		},
	},
}
</script>
