<template lang="pug">
  div
    el-card
      slot(name="header")
        h2 콘텐츠 정보
      slot(name="body")
        el-row
          el-col(:span="6")
            label | 콘텐츠 ID
            label {{content.contentId}}
          el-col(:span="6")
            label | 업로드 일
            label {{content.uploadedAt}}
          el-col(:span="6")
            label | 콘텐츠 배포
            el-dropdown(trigger='click')
              el-button(type="primary" size='mini') 
                | Dropdown List
                i.el-icon-arrow-down.el-icon--right
              el-dropdown-menu(slot='dropdown')
                el-dropdown-item 배포중
                el-dropdown-item 배포대기

          el-col(:span="6")
            label | 공정 등록
            el-dropdown(trigger='click')
              el-button(type="primary" size='mini') 
                | Dropdown List
                i.el-icon-arrow-down.el-icon--right
              el-dropdown-menu(slot='dropdown')
                el-dropdown-item 등록중
                el-dropdown-item 등록대기
        
        el-row
          el-col(:span="6")
            label | 콘텐츠 이름
            label {{content.contentName}}
          el-col(:span="6")
            label | 업로드 멤버
            label {{content.auth}}
          el-col(:span="6")
          el-col(:span="6")
        
        el-row
          el-col(:span="6")
            label | 콘텐츠 크기
            label {{content.volume}}
          el-col(:span="6")
          el-col(:span="6")

    el-card
      slot(name="header")
        h2 씬 그룹 목록
      slot(name="body")
        inline-table(
          :tableData="sceneGroup.tableData" 
          :tableOption="sceneGroup.tableOption"
        )
</template>

<script>
import InlineTable from '@/components/common/InlineTable'
import currentUploadedContent from '@/data/currentUploadedContent'
import sceneGroup from '@/data/sceneGroup'

export default {
	components: {
		InlineTable,
	},
	data() {
		return {
			content: {},
			sceneGroup,
			currentUploadedContent,
		}
	},
	mounted() {
		this.content = currentUploadedContent.tableData.find(c => {
			return c.contentId === this.$route.params.id
		})
	},
}
</script>
