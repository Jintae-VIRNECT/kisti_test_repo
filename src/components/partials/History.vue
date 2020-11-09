<template>
  <section class="history" :class="{ loading: loading }">
    <div class="history__header">
      <div class="history__header--text index">
        <span
          @click="setSort('index')"
          :class="{ active: sort.column === 'index' }"
          >No</span
        >
      </div>
      <div class="history__header--text collabo-name">
        <span
          @click="setSort('collabo-name')"
          :class="{ active: sort.column === 'collabo-name' }"
        >
          {{ $t('list.room_title') }}
        </span>
      </div>
      <div class="history__header--text leader-name">
        <span
          @click="setSort('leader-name')"
          :class="{ active: sort.column === 'leader-name' }"
          >{{ $t('list.room_leader') }}</span
        >
      </div>
      <div class="history__header--text start-date">
        <span
          @click="setSort('start-date')"
          :class="{ active: sort.column === 'start-date' }"
          >{{ $t('list.room_active_date') }}</span
        >
      </div>
      <div class="history__header--text state">
        <span
          @click="setSort('state')"
          :class="{ active: sort.column === 'state' }"
          >{{ $t('list.room_status') }}</span
        >
      </div>
      <div class="history__header--text count">
        <span
          @click="setSort('server-count')"
          :class="{ active: sort.column === 'server-count' }"
          >{{ $t('list.room_server_record') }}</span
        >
      </div>
      <div class="history__header--text count">
        <span
          @click="setSort('local-count')"
          :class="{ active: sort.column === 'local-count' }"
          >{{ $t('list.room_local_record') }}</span
        >
      </div>
      <div class="history__header--text count">
        <span
          @click="setSort('file-count')"
          :class="{ active: sort.column === 'file-count' }"
          >{{ $t('list.room_attach_file') }}</span
        >
      </div>
    </div>

    <div class="history__body" :class="{ nodata: !listExists }">
      <span v-if="loading" class="history__body--loading"></span>
      <template v-else-if="!loading && listExists">
        <div
          class="history__row"
          v-for="(history, index) in historys"
          :key="index"
          @click="showHistory(history.sessionId)"
          @mouseover="hover = true"
          @mouseleave="hover = false"
        >
          <div class="history__text index">
            <p>{{ history.index }}</p>
          </div>
          <div class="history__text collabo-name">
            <p>{{ history.title }}</p>
          </div>
          <div class="history__text leader-name">
            <p>
              {{ history.leader.nickName }}
            </p>
          </div>
          <div class="history__text start-date">
            {{ date(history.activeDate) }}
          </div>
          <div class="history__text state">
            <collabo-status :status="history.status"> </collabo-status>
          </div>
          <div class="history__text count">
            <count-button
              :count="history.serverRecord.length"
              :images="{
                select: require('assets/image/ic_rec_select.svg'),
                active: require('assets/image/ic_rec_active.svg'),
                default: require('assets/image/ic_rec_default.svg'),
              }"
              @click="showInfo('server', history, index)"
            ></count-button>
          </div>
          <div class="history__text count">
            <count-button
              :count="history.localRecord.length"
              :images="{
                select: require('assets/image/ic_video_select.svg'),
                active: require('assets/image/ic_video_active.svg'),
                default: require('assets/image/ic_video_default.svg'),
              }"
              @click="showInfo('local', history, index)"
            ></count-button>
          </div>
          <div class="history__text count">
            <count-button
              :count="history.files.length"
              :images="{
                select: require('assets/image/ic_file_select.svg'),
                active: require('assets/image/ic_file_active.svg'),
                default: require('assets/image/ic_file_default.svg'),
              }"
              @click="showInfo('attach', history, index)"
            ></count-button>
          </div>
        </div>
        <history-info
          :sessionId="sessionId"
          :visible.sync="historyInfo"
        ></history-info>

        <file-info
          type="server"
          :width="'64.2857rem'"
          :height="'50.4286rem'"
          :title="historyTitle"
          :fileList="fileList"
          :visible.sync="serverRecord"
          :deletable="isMaster"
          :headers="getHeader('server')"
          :columns="getColumns('server')"
          :downloadItems="downloadServer"
          :deleteItems="deleteServer"
          :renderOpts="getRenderer('server')"
          :showToggleHeader="true"
          :showPlayButton="true"
        ></file-info>

        <file-info
          type="local"
          :width="'64.2857rem'"
          :height="'50.4286rem'"
          :title="historyTitle"
          :fileList="fileList"
          :visible.sync="localRecord"
          :deletable="isMaster"
          :headers="getHeader('local')"
          :columns="getColumns('local')"
          :downloadItems="downloadLocal"
          :deleteItems="deleteLocal"
          :renderOpts="getRenderer('local')"
          :showToggleHeader="true"
          :showPlayButton="true"
        >
        </file-info>

        <file-info
          type="attach"
          :width="'64.2857rem'"
          :height="'50.4286rem'"
          :title="historyTitle"
          :fileList="fileList"
          :visible.sync="file"
          :deletable="isMaster"
          :headers="getHeader('attach')"
          :columns="getColumns('attach')"
          :downloadItems="downloadItems"
          :deleteItems="deleteItems"
          :renderOpts="getRenderer('attach')"
          :showToggleHeader="true"
          :showPlayButton="false"
        >
        </file-info>
      </template>
      <span v-else class="history__body--nodata">{{ $t('list.no_data') }}</span>
    </div>
  </section>
</template>

<script>
import CollaboStatus from 'CollaboStatus'
import CountButton from 'CountButton'

import HistoryInfo from 'components/modal/HistoryInfo'

import FileInfo from 'components/modal/FileInfo'

import { mapActions } from 'vuex'

import tableMixin from 'mixins/table'
import confirmMixin from 'mixins/confirm'

export default {
  name: 'History',
  mixins: [confirmMixin, tableMixin],
  components: {
    CountButton,
    CollaboStatus,
    HistoryInfo,
    FileInfo,
  },
  props: {
    historys: {
      type: Array,
      default: () => {},
    },
    isMaster: {
      type: Boolean,
      default: false,
    },
    loading: {
      type: Boolean,
    },
  },
  data() {
    return {
      sessionId: '',
      historyInfo: false,
      historyTitle: '',

      //modal flag
      file: false,
      serverRecord: false,
      localRecord: false,

      //modal data
      fileList: [],
      currentIndex: null,

      hover: false,
      sort: { column: '', direction: '' },
    }
  },
  watch: {
    historys() {
      if (this.currentIndex !== null) {
        if (this.file) {
          this.fileList = this.historys[this.currentIndex].files
        } else if (this.serverRecord) {
          this.fileList = this.historys[this.currentIndex].serverRecord
        } else if (this.localRecord) {
          this.fileList = this.historys[this.currentIndex].localRecord
        }
      }
    },
    loading() {
      this.file = false
      this.serverRecord = false
      this.localRecord = false
    },
  },
  computed: {
    listExists() {
      return this.historys.length > 0
    },
  },
  methods: {
    ...mapActions(['setSearch']),
    date(activeDate) {
      return this.$dayjs(activeDate + '+00:00')
        .local()
        .calendar(null, {
          sameDay: 'A h:mm',
          lastDay: this.$t('date.lastday'),
          nextDay: this.$t('date.nextday'),
          lastWeek: 'YYYY.MM.DD',
          sameElse: 'YYYY.MM.DD',
        })
    },
    showHistory(sessionId) {
      this.sessionId = sessionId
      this.historyInfo = true
    },
    showInfo(type, history, index) {
      switch (type) {
        case 'server':
          this.serverRecord = true
          this.fileList = history.serverRecord
          break
        case 'local':
          this.localRecord = true
          this.fileList = history.localRecord
          break
        case 'attach':
          this.file = true
          this.fileList = history.files
          break
      }
      this.historyTitle = history.title
      this.currentIndex = index
    },

    setSort(column) {
      if (this.sort.column === column) {
        if (this.sort.direction === '') {
          this.sort.direction = 'asc'
        } else if (this.sort.direction === 'asc') {
          this.sort.direction = 'desc'
        } else if (this.sort.direction === 'desc') {
          this.sort.direction = ''
          this.sort.column = ''
        }
      } else {
        this.sort.column = column
        this.sort.direction = 'asc'
      }

      this.setSearch({
        sort: {
          column: this.sort.column,
          direction: this.sort.direction,
        },
      })
    },
  },
}
</script>

<style lang="scss">
.history {
  position: relative;
  width: 100%;
  border: 1px solid #eaedf3;
  box-shadow: 0px 1px 0px 0px #eaedf3;
}

.history__body {
  height: 32rem;
  background-color: #ffffff;
  &.nodata {
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

.history__body--loading {
  color: transparent;
  &:after {
    position: absolute;
    top: 50%;
    left: 50%;
    width: 5.7143rem;
    height: 5.7143rem;
    background: center center/40px 40px no-repeat url(~assets/image/loading.gif);
    transform: translate(-50%, -50%);
    content: '';
  }
}

.history__body--nodata {
  color: #0b1f48;
  font-weight: 500;
  font-size: 1.2857rem;
}

.history__header {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 3.1429rem;
  background-color: #ffffff;
  border-bottom: 1px solid #eaedf3;
  box-shadow: 0px 1px 0px 0px #eaedf3;
}
.history__header--text {
  color: #4a5361;
  font-weight: 500;
  font-size: 0.8571rem;
  text-align: center;

  & > span {
    &:hover {
      cursor: pointer;
    }

    &.active {
      font-weight: bold;
    }

    &::after {
      display: inline-block;
      width: 1rem;
      height: 0.7143rem;
      background: url(~assets/image/ic_list_up.svg) center/100% no-repeat;
      content: '';
    }
  }

  &.index {
    width: 7.1429rem;
  }

  &.collabo-name {
    width: 21.4286rem;
    text-align: left;
  }

  &.leader-name {
    width: 14.2857rem;
    text-align: left;
  }

  &.start-date {
    width: 12.1429rem;
  }

  &.state {
    width: 8.5714rem;
    text-align: left;
  }

  &.count {
    width: 8.5714rem;
  }
}

.history__row {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 4.5714rem;
  box-shadow: 0px 1px 0px 0px #eaedf3;
  &:hover {
    background-color: #f5f9ff;
    cursor: pointer;
  }
}
.history__text {
  color: #0b1f48;
  font-weight: 600;
  font-size: 1.0714rem;
  text-align: center;
  opacity: 0.9;

  &:hover {
    cursor: pointer;
  }

  &.index {
    width: 7.1429rem;
    color: #757f91;
    font-weight: 500;
    font-size: 1.0714rem;
    text-align: center;
  }

  &.collabo-name {
    width: 21.4286rem;
    text-align: left;

    & > p {
      // width: 20.4286rem;
      width: 100%;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
    }
  }

  &.leader-name {
    width: 14.2857rem;
    text-align: left;
  }

  &.start-date {
    width: 12.1429rem;
  }

  &.state {
    width: 8.5714rem;
    text-align: left;
  }

  &.count {
    width: 8.5714rem;
  }
}

.history__empty {
  width: 100%;
  height: 36.5714rem;
}
</style>
