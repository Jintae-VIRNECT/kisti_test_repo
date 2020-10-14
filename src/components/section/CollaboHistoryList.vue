<template>
  <section class="collabo-history-list">
    <div class="collabo-history-list__header">
      <span class="collabo-history-list__header--title">협업 내역 목록</span>
      <span class="collabo-history-list__header--description">
        최근 협업 기록을 보여줍니다.
      </span>
      <button class="collabo-history-list__header--excel">EXCEL</button>
    </div>
    <history :historys="historyList"></history>
    <pagination-tool
      @current-page="getHistoryPage"
      :totalPage="pageMeta.totalPage"
    ></pagination-tool>
  </section>
</template>

<script>
import History from 'components/partials/History'
import PaginationTool from 'components/partials/PaginationTool'

import { getHistoryList } from 'api/http/history'

import confirmMixin from 'mixins/confirm'
import searchMixin from 'mixins/filter'

export default {
  name: 'CollaboHistoryList',
  mixins: [searchMixin, confirmMixin],
  components: {
    History,
    PaginationTool,
  },
  data() {
    return {
      targetId: 0,
      modalVisible: false,
      fileList: [],
      historyList: [],
      pageMeta: {
        currentPage: 0,
        currentSize: 0,
        totalElements: 0,
        totalPage: 0,
        last: false,
      },
      paging: false,
      loading: false,
    }
  },
  computed: {},
  watch: {
    workspace(val, oldVal) {
      if (val.uuid !== oldVal.uuid && val.uuid) {
        this.init()
      }
    },
  },
  methods: {
    async init(page = 0) {
      this.loading = true
      const list = await this.getHistory(page)
      if (list === false) {
        this.loading = false
        return
      }
      this.historyList = list.sort((roomA, roomB) => {
        return (
          new Date(roomB.activeDate).getTime() -
          new Date(roomA.activeDate).getTime()
        )
      })
      this.setIndex()
      this.setLeader()
      this.loading = false
      this.$eventBus.$emit('scroll:reset:workspace')
    },
    async getHistoryPage(page) {
      this.init(page - 1)
    },
    async getHistory(page = 0) {
      try {
        const datas = await getHistoryList({
          userId: this.account.uuid,
          workspaceId: this.workspace.uuid,
          paging: true,
          page,
        })
        if ('pageMeta' in datas) {
          this.pageMeta = datas.pageMeta
        } else {
          this.pageMeta = {
            currentPage: 0,
            currentSize: 0,
            totalElements: 0,
            totalPage: 0,
            last: false,
          }
        }
        return datas.roomHistoryInfoList
      } catch (err) {
        console.error(err)
        return false
      }
    },
    setIndex() {
      const startIndex = this.pageMeta.currentPage * 7
      this.historyList.map((history, index) => {
        history.index = index + startIndex
        history.index++
      })
    },
    setLeader() {
      this.historyList.map(history => {
        const leader = history.memberList.find(member => {
          return member.memberType === 'LEADER'
        })
        history.leader = leader
      })
    },
  },

  mounted() {
    if (this.workspace.uuid) {
      this.init()
    }
  },
  beforeDestroy() {},
}
</script>

<style lang="scss">
.collabo-history-list {
  margin-top: 41px;
}
.collabo-history-list__header {
  position: relative;
  height: 54px;
}

.collabo-history-list__header--title {
  margin-right: 10px;
  color: rgb(11, 31, 72);
  font-weight: 500;
  font-size: 22px;
}

.collabo-history-list__header--description {
  color: rgb(122, 122, 122);
  font-weight: 500;
  font-size: 15px;
}
.collabo-history-list__header--excel {
  position: absolute;
  right: 0px;
  width: 89px;
  height: 42px;
  color: rgb(15, 117, 245);
  font-weight: 500;
  font-size: 15px;
  line-height: 20px;
  background: rgb(255, 255, 255);
  border: 1px solid rgb(227, 227, 227);
  border-radius: 2px;
}
</style>
