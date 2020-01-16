<template lang="pug">
	div
		inline-table(
			:tableData="currentUploadedContent" 
			:tableOption="currentUploadedContentTableOption"
		)
			templete(slot="header-right")
				.inline-table__header--right
					span.prefix 업로드된 컨텐츠 
					span.value 102
					span.suffix &nbsp;projects
					.divider
					span.prefix 배포중인 컨텐츠 수 컨텐츠 
					span.value 22
					span.suffix &nbsp;projects
				
</template>
<style lang="scss">
.inline-table__header--right {
	text-align: right;
	> * {
		vertical-align: middle;
	}
	.divider {
		width: 1px;
		height: 20px;
		opacity: 0.82;
		background-color: #cdd1d6;
		display: inline-block;
		margin: 0px 20px;
	}
	.prefix {
		font-size: 14px;
		font-weight: 500;
		line-height: 2;
		color: #0d2a58;
	}
	.value {
		font-size: 18px;
		font-weight: 500;
		line-height: 1.56;
		color: #0065e0;
	}
	.suffix {
		font-size: 12px;
		font-weight: 500;
		line-height: normal;
		color: #566173;
	}
}
</style>

<script>
// UI component
import ProgressCard from '@/components/home/ProgressCard.vue'
import InlineTable from '@/components/common/InlineTable.vue'

/// data
import currentUploadedContent from '@/data/currentUploadedContent'
import currentReportedDetailProcess from '@/data/currentReportedDetailProcess'

const currentUploadedContentTableOption = {
	rowIdName: 'contentId',
	subdomain: '/contents',
	colOptions: true,
	title: '콘텐츠 목록',
	colSetting: [
		{
			prop: 'contentId',
			label: '콘텐츠 ID',
		},
		{
			prop: 'contentName',
			label: '콘텐츠 이름',
		},
		{
			prop: 'volume',
			label: '크기',
		},
		{
			prop: 'uploadedAt',
			label: '업로드일',
		},
		{
			prop: 'auth',
			label: '업로드 멤버',
		},
		{
			prop: 'contentPublish',
			label: '콘텐츠 배포',
		},
		{
			prop: 'processRegister',
			label: '공정 등록',
		},
	],
}

export default {
	components: { ProgressCard, InlineTable },
	data() {
		return {
			value1: '',
			progressData: {
				overallProgressPercent: 90,
				progressByDay: [5, 10, 20, 30, 40, 66, 20],
				progressByDayLastDate: '2020.01.13',
			},
			currentUploadedContent,
			currentReportedDetailProcess,
			currentUploadedContentTableOption,
		}
	},
}
</script>
