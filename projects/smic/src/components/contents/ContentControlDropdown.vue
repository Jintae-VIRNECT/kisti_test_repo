<template lang="pug">
	el-dropdown(trigger="click")
		span.el-dropdown-link
			i.el-icon-more.el-icon--right
		el-dropdown-menu(slot='dropdown')
			.group 배포 설정
			el-dropdown-item 
				.publish-boolean.publishing(@click="$emit('onChangeData', 'publishing')") 
					span 배포중
					span.check(v-if="contentPublish == 'publishing'")
						i.el-icon-check 
			el-dropdown-item 
				.publish-boolean.stop(@click="$emit('onChangeData', 'stop')") 
					span 배포중지
					span.check(v-if="contentPublish == 'stop'")
						i.el-icon-check 
			el-dropdown-item 
				.publish-boolean.managing(@click="$emit('onChangeData', 'managing')") 
					span 공정관리중
					span.check(v-if="contentPublish == 'managing'")
						i.el-icon-check 
			.group 콘텐츠 설정
			el-dropdown-item
				.color-red(@click="onDelete") 삭제
</template>
<style lang="scss" scoped>
.group {
  font-size: 12px;
  font-weight: 500;
  line-height: 1.5;
  color: #566173;
  padding: 0px 16px 4px;
  margin-top: 5px;
  &:last-child {
    margin-top: 0px;
  }
}
.check {
  margin-left: 15px;
  color: black;
  i {
    font-weight: bolder;
  }
}
</style>
<script>
export default {
  props: ['contentPublish'],
  data() {
    return {
      toggleProcessNewModal: false,
    }
  },
  methods: {
    onDelete() {
      this.$confirm(
        '선택한 콘텐츠를 삭제하시겠습니까? 삭제된 콘텐츠는 복구할 수 없습니다',
        '콘텐츠 삭제',
        {
          confirmButtonText: '확인',
          cancelButtonText: '취소',
        },
      )
        .then(() => {
          this.$emit('onChangeData', 'delete')
        })
        .catch(() => {})
    },
  },
}
</script>
