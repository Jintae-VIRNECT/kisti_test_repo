<template lang="pug">
  .process-new
    page-tab-nav
      template(slot='page-nav--right')
        search-tab-nav.search-wrapper.text-right(placeholder="콘텐츠 이름, 등록 멤버 이름" :search="search" :filter="filter" :sort="sort")           
    inline-table(:setSubHeader="true")
      template(slot="header--secondary")
        router-link.title(to="/contents")
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

// model
import { tableColSettings } from '@/models/home'
import { processStatus } from '@/models/process'
import { sortOptions } from '@/models/index'

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
  },
  data() {
    return {
      search: 'smic',
      colSetting: tableColSettings.contents,
      filter: {
        options: [
          {
            value: 'All',
            label: '전체',
          },
          ...processStatus,
        ],
        value: ['All'],
      },
      sort: {
        options: sortOptions,
        value: null,
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
  },
  created() {
    this.$store.dispatch('getContentsList')
  },
}
</script>
