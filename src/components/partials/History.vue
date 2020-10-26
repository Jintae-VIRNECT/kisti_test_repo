<template>
  <section>
    <div class="history">
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
            >협업명</span
          >
        </div>
        <div class="history__header--text leader-name">
          <span
            @click="setSort('leader-name')"
            :class="{ active: sort.column === 'leader-name' }"
            >리더</span
          >
        </div>
        <div class="history__header--text start-date">
          <span
            @click="setSort('start-date')"
            :class="{ active: sort.column === 'start-date' }"
            >협업 시작일</span
          >
        </div>
        <div class="history__header--text state">
          <span
            @click="setSort('state')"
            :class="{ active: sort.column === 'state' }"
            >상태</span
          >
        </div>
        <div class="history__header--text count">
          <span
            @click="setSort('server-count')"
            :class="{ active: sort.column === 'server-count' }"
            >서버 녹화</span
          >
        </div>
        <div class="history__header--text count">
          <span
            @click="setSort('local-count')"
            :class="{ active: sort.column === 'local-count' }"
            >로컬 녹화</span
          >
        </div>
        <div class="history__header--text count">
          <span
            @click="setSort('file-count')"
            :class="{ active: sort.column === 'file-count' }"
            >첨부 파일</span
          >
        </div>
      </div>

      <div class="history__body" :class="{ nodata: !listExists }">
        <template v-if="listExists">
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
                @click="showServerRecord(history)"
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
                @click="showLocalRecord(history)"
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
                @click="showFile(history)"
              ></count-button>
            </div>
          </div>
          <history-info
            :sessionId="sessionId"
            :visible.sync="historyInfo"
          ></history-info>
          <server-record-info
            :title="historyTitle"
            :fileList="fileList"
            :visible.sync="serverRecord"
          ></server-record-info>
          <local-record-info
            :title="historyTitle"
            :fileList="fileList"
            :visible.sync="localRecord"
          ></local-record-info>
          <attach-file-info
            :title="historyTitle"
            :fileList="fileList"
            :visible.sync="file"
          ></attach-file-info>
        </template>
        <span v-else class="history__body--nodata"
          >검색된 결과가 없습니다.</span
        >
      </div>
    </div>
  </section>
</template>

<script>
import CollaboStatus from 'CollaboStatus'
import CountButton from 'CountButton'

import HistoryInfo from 'components/modal/HistoryInfo'

import ServerRecordInfo from 'components/modal/ServerRecordInfo'
import LocalRecordInfo from 'components/modal/LocalRecordInfo'
import AttachFileInfo from 'components/modal/AttachFileInfo'

import { mapActions } from 'vuex'

export default {
  name: 'History',
  components: {
    CountButton,
    CollaboStatus,
    HistoryInfo,
    ServerRecordInfo,
    LocalRecordInfo,
    AttachFileInfo,
  },
  props: {
    historys: {
      type: Array,
      default: () => {},
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

      hover: false,
      sort: { column: '', direction: '' },
    }
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
          // lastDay: this.$t('date.lastday'),
          // nextDay: this.$t('date.nextday'),
          lastDay: '어제',
          nextDay: '내일',
          lastWeek: 'YYYY.MM.DD',
          sameElse: 'YYYY.MM.DD',
        })
    },
    showHistory(sessionId) {
      this.sessionId = sessionId
      this.historyInfo = true
    },
    showServerRecord(history) {
      this.serverRecord = true
      this.fileList = history.serverRecord
      this.historyTitle = history.title
    },
    showLocalRecord(history) {
      this.localRecord = true
      this.fileList = history.localRecord
      this.historyTitle = history.title
    },
    showFile(history) {
      this.file = true
      this.fileList = history.files
      this.historyTitle = history.title
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
  width: 100%;
  border: 1px solid #eaedf3;
  box-shadow: 0px 1px 0px 0px #eaedf3;
}

.history__body {
  height: 448px;
  background-color: #ffffff;
  &.nodata {
    display: flex;
    align-items: center;
    justify-content: center;
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
      width: 14px;
      height: 10px;
      background: url(~assets/image/ic_list_up.svg) center/100% no-repeat;
      content: '';
    }
  }

  &.index {
    width: 100px;
  }

  &.collabo-name {
    width: 300px;
    text-align: left;
  }

  &.leader-name {
    width: 200px;
    text-align: left;
  }

  &.start-date {
    width: 170px;
  }

  &.state {
    width: 120px;
    text-align: left;
  }

  &.count {
    width: 120px;
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
    width: 100px;
    color: #757f91;
    font-weight: 500;
    font-size: 14.9996px;
    text-align: center;
  }

  &.collabo-name {
    width: 300px;
    text-align: left;

    & > p {
      width: 286px;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
    }
  }

  &.leader-name {
    width: 200px;
    text-align: left;
  }

  &.start-date {
    width: 170px;
  }

  &.state {
    width: 120px;
    text-align: left;
  }

  &.count {
    width: 120px;
  }
}

.history__empty {
  width: 100%;
  height: 36.5714rem;
}
</style>
