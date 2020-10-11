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
        <div class="history__header--text record">상태</div>
        <div class="history__header--text record">서버 녹화</div>
        <div class="history__header--text record">로컬 녹화</div>
        <div class="history__header--text record">첨부 파일</div>
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
            <div class="history__text user-name">
              <p>{{ userItem.userName }}</p>
            </div>
            <div class="history__text cooperation-name">
              <p>{{ userItem.cooperateName }}</p>
            </div>
            <div class="history__text start-date">
              {{ userItem.startDate }}
            </div>
            <div class="history__text record">
              <count-button
                :count="userItem.recordCount"
                :serialNum="userItem.serialNum"
              ></count-button>
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
import CountButton from 'CountButton'
export default {
  name: 'History',
  components: {
    CountButton,
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

  &.index {
    width: 12.1429rem;
  }

  &.collabo-name {
    width: 14.3571rem;
    text-align: left;
  }

  &.leader-name {
    width: 26.3571rem;
    text-align: left;
  }

  &.start-date {
    width: 26.4286rem;
  }

  &.record {
    width: 18.3571rem;
  }
}

.history__row {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 4.5714rem;
  // border-color: 1px #eaedf3;
  box-shadow: 0px 1px 0px 0px #eaedf3;
}
.history__text {
  color: #0b1f48;
  font-weight: 600;
  font-size: 1.0714rem;
  text-align: center;
  opacity: 0.9;

  &.index {
    width: 12.1429rem;
    color: #757f91;
    font-weight: 500;
    font-size: 1.0714rem;
    text-align: center;
  }

  &.user-name {
    width: 14.3571rem;
    text-align: left;

    & > p {
      width: 10.9286rem;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
    }
  }

  &.cooperation-name {
    width: 26.3571rem;
    text-align: left;

    & > p {
      width: 22.7857rem;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
    }
  }

  &.start-date {
    width: 26.4286rem;
  }

  &.record {
    width: 18.3571rem;
  }
}

.history__empty {
  width: 100%;
  height: 36.5714rem;
}
</style>
