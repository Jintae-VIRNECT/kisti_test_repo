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
            v-for="(userItem, index) in datas"
            :key="index"
          >
            <div class="history__text index">
              <p>{{ userItem.index }}</p>
            </div>
            <div class="history__text collabo-name">
              <p>{{ userItem.cooperateName }}</p>
            </div>
            <div class="history__text leader-name">
              <p>{{ userItem.leader }}</p>
            </div>
            <div class="history__text start-date">
              {{ userItem.startDate }}
            </div>
            <div class="history__text state">
              <collabo-status :status="userItem.status"> </collabo-status>
            </div>
            <div class="history__text count">
              <server-record-count-button
                :count="userItem.recordCount"
                :serialNum="userItem.serialNum"
              ></server-record-count-button>
            </div>
            <div class="history__text count">
              <local-record-count-button
                :count="userItem.recordCount"
                :serialNum="userItem.serialNum"
              ></local-record-count-button>
            </div>
            <div class="history__text count">
              <file-count-button
                :count="userItem.recordCount"
                :serialNum="userItem.serialNum"
              ></file-count-button>
            </div>
          </div>
        </template>
        <span v-else class="history__body--nodata"
          >검색된 결과가 없습니다.</span
        >
      </div>
    </div>
  </section>
</template>

<script>
import ServerRecordCountButton from 'ServerRecordCountButton'
import LocalRecordCountButton from 'LocalRecordCountButton'
import FileCountButton from 'FileCountButton'
import CollaboStatus from 'CollaboStatus'
export default {
  name: 'History',
  components: {
    CollaboStatus,
    ServerRecordCountButton,
    LocalRecordCountButton,
    FileCountButton,
  },
  props: {
    datas: {
      type: Array,
      default: () => {},
    },
  },
  data() {
    return {}
  },
  computed: {
    listExists() {
      return this.datas.length > 0
    },
  },
  methods: {
    showList() {},
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

  // &.user-name {
  //   width: 286px;
  //   text-align: left;

  //   & > p {
  //     width: 10.9286rem;
  //     overflow: hidden;
  //     white-space: nowrap;
  //     text-overflow: ellipsis;
  //   }
  // }

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
