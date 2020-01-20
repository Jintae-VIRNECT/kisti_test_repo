<template lang="pug">
  .card
    .process-dash-banner
      //- a.toggle-topic-btn(href="#" @click.prevent="toggleTopic")
        img(src="~@/assets/image/ic-graph.svg")
        span {{topic === 'table' ? '그래프' : '테이블'}}
      .box-wrapper
        .box
            .main-title 전체 공정 진행률
            .total-process-progress
              .main-percent 88%
              el-progress(:percentage="88" :show-text="false")
        .box
          .main-title 진행 상태 별 공정 수 
          
          .box-wrapper
            .box
              .sub-title 시작 대기 공정
              .item-container
                .item
                  el-tag.custom.gray 대기
                  span.value 14
            .box
              .sub-title 시작된 공정
              .item-container
                .item
                  el-tag.custom.silver 미진행
                  span.value 3
                .item
                  el-tag.custom.blue 진행
                  span.value 24
                .item
                  el-tag.custom.orange 미흡
                  span.value 14
                .item
                  el-tag.custom.green 완료
                  span.value 44
            .box
              .sub-title 마감된 공정
              .item-container
                .item
                  el-tag.custom.dark-gray 미완수
                  span.value 14
                .item
                  el-tag.custom.dark-blue 완수
                  span.value 24
                .item
                  el-tag.custom.dark-red 결함
                  span.value 14

</template>
<style lang="scss" scoped>
.process-dash-banner {
	padding: 24px 30px;
	.toggle-topic-btn {
		float: right;
	}
	.box-wrapper {
		display: inline-flex;
		.box {
			&:first-child {
				padding-left: 0px;
			}
			padding: 0px 30px;
			position: relative;
			border-right: 1px solid rgba(205, 209, 214, 0.5);
			&:last-child {
				border-right: none;
			}
			.main-title {
				margin-bottom: 16px;
				font-size: 14px;
				font-weight: 500;
				color: #0d2a58;
			}
			.main-percent {
				font-size: 32px;
				font-weight: 500;
				font-stretch: normal;
				font-style: normal;
				line-height: 1.38;
				letter-spacing: normal;
				color: #006deb;
				margin-bottom: 8px;
			}
			.sub-title {
				font-size: 12px;
				font-weight: 500;
				font-stretch: normal;
				font-style: normal;
				line-height: 1.5;
				letter-spacing: normal;
				color: #566173;
				margin-bottom: 10px;
			}
		}
	}
	.item-container {
		display: flex;
		.item {
			margin-left: 30px;
			&:first-child {
				margin-left: 0px;
			}
			& > * {
				vertical-align: middle;
			}
			.value {
				margin-left: 12px;
				font-size: 24px;
				font-weight: 500;
				font-stretch: normal;
				font-style: normal;
				line-height: normal;
				letter-spacing: normal;
				color: #385370;
			}
		}
	}
}
</style>
<script>
export default {
	data() {
		return {
			topic: 'table',
		}
	},
	methods: {
		toggleTopic() {
			this.topic = this.topic === 'table' ? 'graph' : 'table'
		},
	},
}
</script>
