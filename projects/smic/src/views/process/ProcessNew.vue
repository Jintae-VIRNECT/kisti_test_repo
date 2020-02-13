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
              .content-name(v-if="prop === 'contentName'")
                img.prefix-img(src="~@/assets/image/ic-content.svg")
                span {{tableData[scope.$index][prop]}}
              div(v-else-if="prop === 'status'")
                span.publish-boolean(:class="tableData[scope.$index][prop]") {{tableData[scope.$index][prop] | publishBoolean}}
              .auth-wrapper(v-else-if="prop === 'uploaderName'")
                .auth-img(:style="{'background-image': `url(${tableData[scope.$index]['uploaderProfile']})`}")
                span {{tableData[scope.$index][prop]}}
              div(v-else-if="prop === 'uploadDate'")
                span {{tableData[scope.$index][prop] | dayJs_FilterDateTime}}
              div(v-else)
                span {{ tableData[scope.$index][prop]}}
    process-new-modal(
      :target='target'
      :toggleProcessModal="toggleNewModal"
      @handleConfirm="onNewModalConfirm"
      @onToggleNewModal="onToggleNewModal")
    process-control-dropdown-modal(
      :target="target"
      modalType="create"
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

// model
import { tableColSettings } from '@/models/home'
import { processStatus } from '@/models/process'
import { sortOptions } from '@/models/index'

// mixin
import contentList from '@/mixins/contentList'
import dayjs from '@/utils/dayjs'

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
  },
  data() {
    return {
      tableData: this.$store.getters.getCurrentContentsList,
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
      target: {},
    }
  },
  methods: {
    onClickCell(row) {
      this.toggleNewModal = true
      this.target = row
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
}
</script>
