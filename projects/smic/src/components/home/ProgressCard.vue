<template lang="pug">
	el-card
		.card
			.card__header
				span SMIC 스마트툴
				router-link.more(to="/" style="float: right; padding: 3px 0" type="text") 더보기
			.card-body
				p
					span 평균 진행률
					span(style="float: right; padding: 3px 0" type="text") {{progressData.overallProgressPercent}}%
				el-progress(:percentage="progressData.overallProgressPercent" :show-text="false")
				p
					span 일자별 진행률
					span(style="float: right; padding: 3px 0" type="text") {{progressData.progressByDayLastDate}}
				#lineChart
</template>

<script>
import bb from 'billboard.js'

export default {
	props: {
		progressData: {
			overallProgressPercent: Number,
			progressByDay: Array,
			progressByDayLastDate: String,
		},
	},
	mounted() {
		bb.generate({
			data: {
				columns: [
					['data1', 30, 200, 100, 400, 150, 250],
					['data2', 50, 20, 10, 40, 15, 25],
				],
			},
			bindto: '#lineChart',
		})
	},
}
</script>
