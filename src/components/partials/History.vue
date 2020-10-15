<template>
  <section>
    <div class="history">
      <div class="history__header">
        <div class="history__header--text index">
          No
        </div>
        <div class="history__header--text collabo-name">
          협업명
        </div>
        <div class="history__header--text leader-name">
          리더
        </div>
        <div class="history__header--text start-date">협업 시작일</div>
        <div class="history__header--text state">상태</div>
        <div class="history__header--text count">서버 녹화</div>
        <div class="history__header--text count">로컬 녹화</div>
        <div class="history__header--text count">첨부 파일</div>
      </div>

      <div class="history__body" :class="{ nodata: !listExists }">
        <template v-if="listExists">
          <div
            class="history__row"
            v-for="(history, index) in historys"
            :key="index"
            @click="openHistoryInfo(history.sessionId)"
          >
            <div class="history__text index">
              <p>{{ history.index }}</p>
            </div>
            <div class="history__text collabo-name">
              <p>{{ history.title }}</p>
            </div>
            <div class="history__text leader-name">
              <p>{{ history.leader.nickName }}</p>
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
              ></count-button>
            </div>
          </div>
          <history-info
            :sessionId="sessionId"
            :visible.sync="showHistoryInfo"
          ></history-info>
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
import HistoryInfo from 'components/modal/HistoryInfo'
import CountButton from 'CountButton'
export default {
  name: 'History',
  components: {
    CountButton,
    CollaboStatus,
    HistoryInfo,
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
      showHistoryInfo: false,
    }
  },
  computed: {
    listExists() {
      return this.historys.length > 0
    },
  },
  methods: {
    showList() {},
    openHistoryInfo(sessionId) {
      console.log(sessionId)
      this.sessionId = sessionId
      this.showHistoryInfo = true
    },
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
  &:hover {
    cursor: pointer;
  }
  &::after {
    display: inline-block;
    width: 14px;
    height: 10px;
    background: url(~assets/image/ic_list_up.svg) center/100% no-repeat;
    content: '';
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
