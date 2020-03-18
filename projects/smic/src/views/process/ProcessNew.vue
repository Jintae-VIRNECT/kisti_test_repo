<template lang="pug">
  .process-new
    page-tab-nav
      template(slot='page-nav--right')
        search-tab-nav.search-wrapper.text-right(placeholder="콘텐츠 이름, 등록 멤버 이름" :search="search" :filter="filter" :sort="sort" @change="onChangeSearch")           
    inline-table(:setSubHeader="true")
      template(slot="header--secondary")
        router-link.title(to="/process")
          i.el-icon-back
          | 공정으로 등록할 콘텐츠 선택
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
              table-column(type="contents" :prop="prop" :data="tableData[scope.$index]")
    pagination(target="contents" :params="params")
    process-new-modal(
      :target="target"
      :toggleProcessModal="toggleNewModal"
      @handleConfirm="onNewModalConfirm"
      @onToggleNewModal="onToggleNewModal")
    process-control-dropdown-modal(
      modalType="create"
      :target="target"
      :toggleProcessModal="toggleProcessModal"
      @onToggleProcessModal="onToggleProcessModal")
</template>
<style lang="scss"></style>

<script>
// UI component
import PageTabNav from '@/components/common/PageTabNav.vue'
import ProgressCard from '@/components/home/ProgressCard.vue'
import InlineTable from '@/components/common/InlineTable.vue'
import PageBreadCrumb from '@/components/common/PageBreadCrumb.vue'
import ProcessNewModal from '@/components/process/ProcessNewModal.vue'
import ProcessControlDropdownModal from '@/components/process/ProcessControlDropdownModal.vue'
import SearchTabNav from '@/components/common/SearchTabNav.vue'
import TableColumn from '@/components/common/TableColumn.vue'
import Pagination from '@/components/common/Pagination.vue'

// model
import { tableColSettings } from '@/models/home'

// mixin
import contentList from '@/mixins/contentList'
import dayjs from '@/plugins/dayjs'

export default {
  mixins: [contentList, dayjs],
  components: {
    ProgressCard,
    InlineTable,
    PageTabNav,
    PageBreadCrumb,
    ProcessNewModal,
    ProcessControlDropdownModal,
    SearchTabNav,
    TableColumn,
    Pagination,
  },
  data() {
    return {
      search: '',
      colSetting: tableColSettings.contents,
      params: {
        size: 9,
      },
      filter: {
        options: [
          {
            value: 'All',
            label: '전체',
          },
          {
            value: '?',
            label: '공정 진행 상태 별',
          },
        ],
        value: ['All'],
      },
      sort: {
        options: [
          {
            value: 'createdDate,desc',
            label: '최신 보고순',
          },
          {
            value: 'createdDate,asc',
            label: '오래된 보고순',
          },
        ],
        value: 'createdDate,desc',
      },
      toggleNewModal: false,
      toggleProcessModal: false,
    }
  },
  computed: {
    tableData() {
      return this.$store.getters.contentsList
    },
    target() {
      return this.$store.getters.contentDetail
    },
  },
  methods: {
    async onClickCell(row) {
      if (row.status === 'MANAGED') {
        this.$alert(
          `해당 콘텐츠로 생성된 공정이 이미 존재합니다.<br>기존에 생성된 공정을 종료하고 시도해주세요.`,
          {
            dangerouslyUseHTMLString: true,
          },
        )
        return false
      }
      this.$store.commit('SET_CONTENT_INFO', row)
      await this.$store.dispatch('getSceneGroupList', row.contentUUID)
      this.toggleNewModal = true
    },
    onToggleNewModal(bool) {
      this.toggleNewModal = bool
    },
    onNewModalConfirm() {
      this.toggleProcessModal = true
    },
    onToggleProcessModal(bool) {
      this.toggleProcessModal = bool
      if (!bool) {
        this.toggleNewModal = false
      }
    },
    onChangeSearch: function(params) {
      this.params = {
        ...this.params,
        ...params,
      }
      this.$store.dispatch('getContentsList', this.params)
    },
  },
  created() {
    this.$store.dispatch('getContentsList', this.params)
  },
}
</script>
