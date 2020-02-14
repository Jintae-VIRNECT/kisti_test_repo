<template lang="pug">
  div
    h1.admin-body__header 이슈관리
    .page-nav
      search-tab-nav.search-wrapper.text-right(placeholder="이름 또는 이메일 검색" :search="search" :filter="filter" :sort="sort" @change="onChangeSearch")
    inline-table(:setMainHeader="true")
      template(slot="header-left")
        span.title 최근 등록된 공정 콘텐츠
      template(slot="header-right")
        .text-right
          router-link.more-link(type="text" to="/contents") 더보기
      template(slot="body")
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
              div(v-if="prop === 'type'")
                span.issue-type {{tableData[scope.$index][prop]}}
              .content-name(v-else-if="prop === 'name'")
                img.prefix-img(src="~@/assets/image/ic-content.svg")
                span {{tableData[scope.$index][prop]}}
              div(v-else-if="prop === 'contentPublish'")
                span.publish-boolean(:class="tableData[scope.$index][prop]") {{tableData[scope.$index][prop] | publishBoolean}}
              .auth-wrapper(v-else-if="prop === 'auth'")
                .auth-img(:style="{'background-image': `url(${tableData[scope.$index]['profileImg']})`}")
                span {{tableData[scope.$index][prop]}}
              div(v-else-if="prop === 'reportedAt'")
                span {{tableData[scope.$index][prop] | dayJs_FilterDateTime}}
              div(v-else)
                span {{ tableData[scope.$index][prop]}}
    issue-modal(
      :toggleIssueModal="toggleIssueModal"
      @handleCancel="onHandleCancel")
      
</template>
<script>
import InlineTable from '@/components/common/InlineTable'
import ContentControlDropdown from '@/components/contents/ContentControlDropdown'
import PageTabNav from '@/components/common/PageTabNav.vue'
import PageHeader from '@/components/common/PageHeader.vue'
import SearchTabNav from '@/components/common/SearchTabNav.vue'
import IssueModal from '@/components/issue/IssueModal.vue'

/// data
import issueData from '@/data/issue'

// model
import tableColSettings from '@/models/issue'

// mixin
import contentList from '@/mixins/contentList'
import dayjs from '@/plugins/dayjs'
export default {
  components: {
    InlineTable,
    ContentControlDropdown,
    PageTabNav,
    PageHeader,
    SearchTabNav,
    IssueModal,
  },
  mixins: [contentList, dayjs],
  data() {
    return {
      tableData: issueData,
      colSetting: tableColSettings,
      search: '',
      filter: {
        options: [
          {
            value: 'All',
            label: '전체',
          },
        ],
        value: ['All'],
      },
      sort: {
        options: [
          {
            value: 'name,asc',
            label: 'ㄱ-ㅎ순',
          },
          {
            value: 'name,desc',
            label: 'ㄱ-ㅎ역순',
          },
        ],
        value: 'name,asc',
      },
      toggleIssueModal: false,
    }
  },
  methods: {
    onClickCell() {
      this.toggleIssueModal = true
    },
    onChangeSearch() {},
    onToggleIssueModal(boolean, type) {
      this.toggleIssueModal = boolean
      this.modalType = type
    },
    onHandleCancel() {
      this.toggleIssueModal = false
    },
  },
}
</script>
