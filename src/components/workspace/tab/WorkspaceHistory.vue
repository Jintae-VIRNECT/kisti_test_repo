<template>
  <tab-view
    title="최근 통화 목록"
    description="최근 통화 목록은 30일 동안 보관됩니다."
    placeholder="협업, 멤버 이름 검색"
    customClass="history"
    :emptyImage="require('assets/image/img_recent_empty.svg')"
    emptyTitle="최근 통화 목록이 없습니다."
    emptyDescription="원격 협업을 시작해보세요."
    :empty="historyList.length === 0"
    :showDeleteButton="true"
    :showRefreshButton="true"
    :deleteButtonText="'전체삭제'"
    :listCount="historyList.length"
    :loading="loading"
    @refresh="getHistory"
    @delete="showDeleteDialog"
  >
    <workspace-history-list :historyList="historyList"></workspace-history-list>
  </tab-view>
</template>

<script>
import TabView from '../partials/WorkspaceTabView'
import WorkspaceHistoryList from '../section/WorkspaceHistoryList'
import { getHistoryList, deleteAllHistory } from 'api/workspace/history'
import auth from 'utils/auth'

import confirmMixin from 'mixins/confirm'

export default {
  name: 'WorkspaceHistory',
  mixins: [confirmMixin],
  components: {
    TabView,
    WorkspaceHistoryList,
  },
  data() {
    return {
      historyList: [],
      loading: false,
    }
  },
  watch: {
    workspace(val, oldVal) {
      if (val.uuid !== oldVal.uuid && val.uuid) {
        this.getHistory()
      }
    },
  },
  methods: {
    showDeleteDialog() {
      this.confirmCancel(
        '모든 목록을 삭제하시겠습니까?',
        {
          text: '삭제하기',
          action: () => {
            this.deleteList()
          },
        },
        { text: '취소' },
      )
    },
    async deleteList() {
      try {
        await deleteAllHistory({
          workspaceId: this.workspace.uuid,
          userId: this.account.uuid,
        })
        this.historyList = []
      } catch (err) {
        console.error(err)
      }
    },
    async getHistory() {
      try {
        this.loading = true
        const datas = await getHistoryList({
          userId: this.account.uuid,
          workspaceId: this.workspace.uuid,
        })
        this.loading = false
        this.historyList = datas.roomHistoryInfoList
      } catch (err) {
        console.error(err)
      }
    },
  },

  mounted() {
    if (this.workspace.uuid) {
      this.getHistory()
    }
  },
}
</script>
