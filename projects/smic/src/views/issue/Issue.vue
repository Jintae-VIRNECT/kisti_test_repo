<template lang="pug">
  div
    h1.admin-body__header 이슈관리
    .page-nav
      search-tab-nav.search-wrapper.text-right(placeholder="공정, 세부 공정, 작업, 멤버 이름" :search="search" :filter="filter" :sort="sort" @change="onChangeSearch")
    inline-table
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
              table-column(:prop="prop" :data="tableData[scope.$index]")
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
import IssueModal from '@/components/process/IssueModal.vue'
import TableColumn from '@/components/common/TableColumn.vue'

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
    TableColumn,
  },
  mixins: [contentList, dayjs],
  data() {
    return {
      tableData: this.$store.getters.issueList,
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
            value: 'createdDate,desc',
            label: '최신 순',
          },
          {
            value: 'createdDate,asc',
            label: '오래된 순',
          },
        ],
        value: 'createdDate,desc',
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
