<template lang="pug">
  div
    .card
      .card__header
        .card__header--left
          span.title 콘텐츠 정보
      .card__body
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

    //- el-card
      slot(name="header")
        h2 씬 그룹 목록
      slot(name="body")
        inline-table(
          :tableData="sceneGroup.tableData" 
          :tableOption="sceneGroup.tableOption"
        )
    .card
      .card__header
        .card__header--left
          span.title 세부공정 콘텐츠 목록 
      .card__body
        slot(name="tabs")
        el-table.inline-table(
          :data='tableData' 
          style='width: 100%'
          @cell-click="onClickCell")
          el-table-column(
            v-for="{label, width, prop} in colSetting" 
            :key="prop" 
            :prop="prop" 
            :label="label" 
            :width="width || ''") 
            template(slot-scope='scope')
              div(v-if="prop == 'index'") 
                span {{scope.$index + 1}}.
              div(v-else)
                span {{ tableData[scope.$index][prop] }}
      //- el-pagination.inline-table-pagination(
      //-   v-if='setPagination'
      //-   :hide-on-single-page='false' 
      //-   :page-size="pageSize" 
      //-   :pager-count="tableOption ? tableOption.pagerCount : 5"
      //-   :total='tableData.length' 
      //-   layout='prev, jumper, next'
      //-   :current-page='currentPage'
      //-   @prev-click='currentPage -= 1'
      //-   @next-click='currentPage += 1'
      //- )
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
      tableData: sceneGroup.tableData,
      colSetting: sceneGroup.tableOption.colSetting,
    }
  },
  mounted() {
    this.content = currentUploadedContent.find(c => {
      return c.contentId === this.$route.params.id
    })
    console.log('colSetting : ', this.colSetting)
  },
  methods: {
    onClickCell() {},
  },
}
</script>
