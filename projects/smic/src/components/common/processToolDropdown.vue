<template lang="pug">
  el-dropdown(trigger="click")
    span.el-dropdown-link
      i.el-icon-more.el-icon--right
    el-dropdown-menu(slot='dropdown')
      el-dropdown-item 
        a(@click="endProcess") 공정 마감
      el-dropdown-item 
        a(@click="addProcess") 공정 추가 생성
      el-dropdown-item 
        a(@click="editProcess") 공정 편집
      el-dropdown-item.color-red(@click.prevent="deleteProcess") 삭제
</template>

<script>
export default {
	props: ['processId', 'detailProcessId', 'workId', 'detailWorkId'],
	methods: {
		endProcess() {
			this.$confirm(
				'선택한 공정을 마감 하시겠습니까? 공정 마감 시 관련 데이터를 더이상 수집하지 않습니다.',
				'공정 마감',
				{
					confirmButtonText: '확인',
					cancelButtonText: '취소',
				},
			)
				.then(() => {})
				.catch(() => {})
		},
		addProcess() {
			this.$confirm(
				'공정 추가 생성',
				`선택한 공정을 추가 생성 하시겠습니까? \n
        추가 생성 시 기존 공정 항목에 데이터를 더이상 수집하지 않고 새로운 공정을 추가하여 새로운 데이터를 수집합니다.`,
				{
					confirmButtonText: '확인',
					cancelButtonText: '취소',
				},
			)
				.then(() => {
					this.$alert(
						'공정 추가 생성 완료',
						`입력하신 정보로 공정을 추가 생성되었습니다. \n
            추가된 공정으로 새로운 보고를 받습니다.`,
						{
							confirmButtonText: '확인',
							callback: action => {
								// this.$message({
								// 	type: 'info',
								// 	message: `action: ${action} \n processId: ${this.$props.processId}`,
								// })
							},
						},
					)
				})
				.catch(() => {})
			// 		callback: action => {
			// 			console.log('action : ', action)
			// 			this.$alert(
			// 				'공정 추가 생성 완료',
			// 				`입력하신 정보로 공정을 추가 생성되었습니다. \n
			//         추가된 공정으로 새로운 보고를 받습니다.`,
			// 				{
			// 					confirmButtonText: 'OK',
			// 					callback: action => {
			// 						// this.$message({
			// 						// 	type: 'info',
			// 						// 	message: `action: ${action} \n processId: ${this.$props.processId}`,
			// 						// })
			// 					},
			// 				},
			// 			)
			// 		},
			// 	},
			// )
		},
		editProcess() {
			this.$confirm(
				'등록 지연으로 입력하신 공정 일정과 등록될 공정 일정이 달라집니다. 설정된 일정을 확인해 주세요.',
				'공정 편집',
				{
					confirmButtonText: '확인',
					cancelButtonText: '취소',
				},
			)
				.then(() => {
					this.$confirm(
						`공정 정보를 편집하시겠습니까? 변경된 정보로 공정 보고를 받습니다.`,
						'공정 편집 완료',
						{
							confirmButtonText: '확인',
							cancelButtonText: '취소',
						},
					)
				})
				.catch(() => {})
			// 		callback: action => {
			// 			console.log('action : ', action)
			// 			this.$alert(
			// 				'공정 추가 생성 완료',
			// 				`입력하신 정보로 공정을 추가 생성되었습니다. \n
			//         추가된 공정으로 새로운 보고를 받습니다.`,
			// 				{
			// 					confirmButtonText: 'OK',
			// 					callback: action => {
			// 						// this.$message({
			// 						// 	type: 'info',
			// 						// 	message: `action: ${action} \n processId: ${this.$props.processId}`,
			// 						// })
			// 					},
			// 				},
			// 			)
			// 		},
			// 	},
			// )
		},
		deleteProcess() {
			this.$confirm(
				`선택한 공정을 삭제 하시겠습니까? \n
        삭제된 공정 관련 데이터를 더이상 수집하지 않습니다. \n
        공정 삭제 시 관련 공정 데이터 기록은 모두 사라지며 복구되지 않습니다. `,
				'공정 삭제',
				{
					confirmButtonText: 'OK',
				},
			)
				.then(() => {})
				.catch(() => {})
		},
	},
}
</script>
