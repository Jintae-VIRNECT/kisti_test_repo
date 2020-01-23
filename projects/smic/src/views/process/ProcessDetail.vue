<template lang="pug">
  div
    el-card
      slot(name="header")
        h2 공정 정보
      slot(name="body")
        el-row
          el-col(:span="6")
            label | 공정 ID
            label {{process.processId}}
          el-col(:span="6")
            label | 업로드 일
            label {{process.uploadedAt}}
          el-col(:span="6")
            label | 공정 배포
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
            label | 공정 이름
            label {{process.processName}}
          el-col(:span="6")
            label | 업로드 멤버
            label {{process.auth}}
          el-col(:span="6")
          el-col(:span="6")
        
        el-row
          el-col(:span="6")
            label | 공정 크기
            label {{process.volume}}
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
import currentReportedDetailProcess from '@/data/currentReportedDetailProcess'
import sceneGroup from '@/data/sceneGroup'

export default {
  components: {
    InlineTable,
  },
  data() {
    return {
      process: {},
      sceneGroup,
      currentReportedDetailProcess,
    }
  },
  mounted() {
    this.process = currentReportedDetailProcess.find(c => {
      return c.processId === this.$route.params.id
    })
  },
}
</script>
