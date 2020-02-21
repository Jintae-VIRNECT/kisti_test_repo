<template lang="pug">
	el-dropdown(trigger="click")
		span.el-dropdown-link
			i.el-icon-more.el-icon--right
		el-dropdown-menu(slot='dropdown')
			//- .group 배포 설정
			//- el-dropdown-item 
			//- 	.publish-boolean.PUBLISH(@click="$emit('onChangeData', 'PUBLISH')") 
			//- 		span 배포중
			//- 		span.check(v-if="data.status == 'PUBLISH'")
			//- 			i.el-icon-check 
			//- el-dropdown-item 
			//- 	.publish-boolean.WAIT(@click="$emit('onChangeData', 'WAIT')") 
			//- 		span 배포중지
			//- 		span.check(v-if="data.status == 'WAIT'")
			//- 			i.el-icon-check 
			//- el-dropdown-item 
			//- 	.publish-boolean.MANAGED(@click="$emit('onChangeData', 'MANAGED')") 
			//- 		span 공정관리중
			//- 		span.check(v-if="data.status == 'MANAGED'")
			//- 			i.el-icon-check 
			.group 콘텐츠 설정
			el-dropdown-item
				.color-red(@click="onDelete") 삭제
</template>
<style lang="scss" scoped>
.group {
  margin-top: 5px;
  padding: 0px 16px 4px;
  color: #566173;
  font-weight: 500;
  font-size: 12px;
  line-height: 1.5;
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
  props: ['data'],
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
          this.$store.dispatch('deleteContent', this.data.contentUUID)
        })
        .catch(() => {})
    },
  },
}
</script>
