<template lang="pug">
  div
    h1.admin-body__header 이슈관리
    .page-nav
      search-tab-nav.search-wrapper.text-right(
        placeholder="공정, 세부공정, 작업, 멤버 이름" 
        :subject="subject" 
        :search="params.search" 
        :filter="filter" 
        :sort="sort" 
        @change="onChangeSearch"
      )
    inline-table
      template(slot="body")
        el-table.inline-table(
          :data='issueList' 
          style='width: 100%'
          @cell-click="onClickCell")
          el-table-column(
            v-for="{label, width, prop} in colSetting" 
            :key="prop" 
            :prop="prop" 
            :label="label" 
            :width="width || ''") 
            template(slot-scope='scope')
              table-column(:prop="prop" :data="issueList[scope.$index]")
    issue-modal(
      :issueId="issueId"
      :toggleIssueModal="toggleIssueModal"
      @handleCancel="onHandleCancel")
      
</template>
<script>
import { mapGetters } from 'vuex'

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
      issueId: 0,
      colSetting: tableColSettings,
      params: {
        search: '',
        size: 10,
      },
      subject: {
        options: [
          {
            value: 'process',
            label: '공정',
          },
          {
            value: 'name',
            label: '멤버 이름',
          },
        ],
        value: 'process',
      },
      filter: {
        options: [
          {
            value: 'All',
            label: '전체',
          },
          {
            value: 'GLOBAL',
            label: '이슈',
          },
          {
            value: 'WORK',
            label: '작업 이슈',
          },
        ],
        value: ['All'],
      },
      sort: {
        options: [
          {
            value: 'updated_at,desc',
            label: '최신 순',
          },
          {
            value: 'updated_at,asc',
            label: '오래된 순',
          },
        ],
        value: 'updated_at,desc',
      },
      toggleIssueModal: false,
    }
  },
  computed: {
    ...mapGetters(['issueList']),
  },
  methods: {
    onClickCell(data) {
      this.issueId = data.issueId
      this.toggleIssueModal = true
    },
    onChangeSearch(params) {
      this.params = {
        ...this.params,
        ...params,
      }
      this.$store.dispatch('getIssueList', this.params)
    },
    onToggleIssueModal(boolean, type) {
      this.toggleIssueModal = boolean
      this.modalType = type
    },
    onHandleCancel() {
      this.toggleIssueModal = false
    },
  },
  created() {
    // route query check
    const query = this.$router.currentRoute.query
    if (query && query.search) {
      this.params.search = query.search
    }
    // issue modal
    if (query && query.issueId) {
      this.issueId = query.issueId * 1
      setTimeout(() => {
        this.toggleIssueModal = true
      }, 200)
    }
    this.$store.dispatch('getIssueList', this.params)
  },
}
</script>
