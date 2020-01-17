<template lang="pug">
  .card
    .process-dash-banner
      a.toggle-topic-btn(href="#" @click.prevent="toggleTopic")
        img(src="~@/assets/image/ic-graph.svg")
        span {{topic === 'table' ? '그래프' : '테이블'}}
      .box-wrapper
        .box
          .main-title 전체 공정 진행률
          .item
            .progress-percent 88%
            el-progress(:percentage="88" :show-text="false")
        .box
          .main-title 진행 상태 별 공정 수 
          
          .box-wrapper
            .box
              .sub-title 시작 대기 공정
              .item
                el-tag 대기
                span 14
            .box
              .sub-title 시작된 공정
              .item-container
                .item
                  el-tag 미진행
                  span 3
                .item
                  el-tag 진행
                  span 24
                .item
                  el-tag 미흡
                  span 14
                .item
                  el-tag 완료
                  span 44
            .box
              .sub-title 마감된 공정
              .item-container
                .item
                  el-tag 미완수
                  span 14
                .item
                  el-tag 완수
                  span 24
                .item
                  el-tag 결함
                  span 14

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
			// display: inline-block;
			border-right: 1px solid rgba(205, 209, 214, 0.5);
			&:last-child {
				border-right: none;
			}
			.main-title {
				font-size: 14px;
				font-weight: 500;
				color: #0d2a58;
			}
		}
	}
	.item-container {
		display: flex;
	}
	.item {
		padding: 16px 30px;
	}
	.sub-title {
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
