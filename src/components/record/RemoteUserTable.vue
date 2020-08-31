<template>
  <section>
    <div class="user-table">
      <div class="user-table__header">
        <div class="user-table__header--text index">
          No
        </div>
        <div class="user-table__header--text user-name">
          사용자
        </div>
        <div class="user-table__header--text cooperation-name">
          협업 이름
        </div>
        <div class="user-table__header--text start-date">협업시작일</div>
        <div class="user-table__header--text record">녹화</div>
      </div>

      <div class="user-table__body" :class="{ nodata: !listExists }">
        <template v-if="listExists">
          <div
            class="user-table__row"
            v-for="(userItem, index) in userList"
            :key="index"
          >
            <div class="user-table__text index">
              <p>{{ userItem.index }}</p>
            </div>
            <div class="user-table__text user-name">
              <p>{{ userItem.userName }}</p>
            </div>
            <div class="user-table__text cooperation-name">
              <p>{{ userItem.cooperateName }}</p>
            </div>
            <div class="user-table__text start-date">
              {{ userItem.startDate }}
            </div>
            <div class="user-table__text record">
              <record-count-button
                :count="userItem.recordCount"
                :serialNum="userItem.serialNum"
              ></record-count-button>
            </div>
          </div>
        </template>
        <span v-else class="user-table__body--nodata"
          >검색된 결과가 없습니다.</span
        >
      </div>
    </div>
  </section>
</template>

<script>
import RecordCountButton from './partials/RemoteRecordCountButton'

export default {
  name: 'RemoteUserTable',
  components: {
    RecordCountButton,
  },
  props: {
    userList: {
      type: Array,
      default: () => [],
    },
  },
  computed: {
    listExists() {
      return this.userList.length > 0
    },
  },

  data() {
    return {}
  },
  methods: {
    showList() {},
  },
}
</script>

<style lang="scss">
.user-table {
  width: 100%;
  border: 1px solid #eaedf3;
  box-shadow: 0px 1px 0px 0px #eaedf3;
}

.user-table__body {
  height: 36.5714rem;
  &.nodata {
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

.user-table__body--nodata {
  color: #0b1f48;
  font-weight: 500;
  font-size: 1.2857rem;
}

.user-table__header {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 3.1429rem;
  box-shadow: 0px 1px 0px 0px #eaedf3;
}
.user-table__header--text {
  color: #4a5361;
  font-weight: 500;
  font-size: 0.8571rem;
  text-align: center;

  &.index {
    width: 12.1429rem;
  }

  &.user-name {
    width: 14.3571rem;
    text-align: left;
  }

  &.cooperation-name {
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

.user-table__row {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 4.5714rem;
  border-color: #eaedf3;
  box-shadow: 0px 1px 0px 0px #eaedf3;
}
.user-table__text {
  color: #0b1f48;
  font-weight: 500;
  font-size: 1.0714rem;
  text-align: center;

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

.user-table__empty {
  width: 100%;
  height: 36.5714rem;
}
</style>
