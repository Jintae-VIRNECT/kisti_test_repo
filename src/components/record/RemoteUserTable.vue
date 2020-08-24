<template>
  <table class="user-table">
    <thead>
      <tr class="user-table__header">
        <th>
          <p class="user-table__header--text">No</p>
        </th>
        <th>
          <p class="user-table__header--text align-left">
            사용자
          </p>
        </th>
        <th><p class="user-table__header--text">협업시작일</p></th>
        <th><p class="user-table__header--text">녹화</p></th>
      </tr>
    </thead>
    <tbody>
      <tr
        class="user-table__row"
        v-for="(userItem, index) in userlist"
        :key="index"
      >
        <td class="user-table__index">
          <p>{{ userItem.index }}</p>
        </td>
        <td class="user-table--user-name">
          <p class="user-table__text align-left">
            {{ userItem.userName }}
          </p>
        </td>
        <td class="">
          <p class="user-table__text">
            {{ userItem.startDate }}
          </p>
        </td>
        <td style="text-align: center;" class="">
          <record-count-button
            :count="userItem.recordCount"
          ></record-count-button>
        </td>
      </tr>
    </tbody>
  </table>

  <!-- 페이징 기능 -->
</template>

<script>
import RecordCountButton from './partials/RemoteRecordCountButton'
export default {
  name: 'RemoteUserTable',
  components: {
    RecordCountButton,
  },
  props: {
    // userlist: {
    //   type: Array,
    //   default: () => [],
    // },
  },

  data() {
    return {
      userlist: [
        {
          index: 1,
          userName:
            '일이삼사아육칠팔구십일이삼사아육칠팔구십일이삼사아육칠팔구십…',
          startDate: '2020.07.30',
          recordCount: 10,
        },
        {
          index: 2,
          userName: 'test1',
          startDate: '2020.07.30',
          recordCount: 10,
        },
        {
          index: 3,
          userName: 'test1',
          startDate: '2020.07.30',
          recordCount: 10,
        },
        {
          index: 4,
          userName: 'test1',
          startDate: '2020.07.30',
          recordCount: 10,
        },
        {
          index: 5,
          userName: 'test1',
          startDate: '2020.07.30',
          recordCount: 0,
        },
        {
          index: 6,
          userName: 'test1',
          startDate: '2020.07.30',
          recordCount: 999,
        },
        {
          index: 7,
          userName: 'test1',
          startDate: '2020.07.30',
          recordCount: 2,
        },
        {
          index: 8,
          userName: 'test1',
          startDate: '2020.07.30',
          recordCount: 10,
        },
      ],
    }
  },
}
</script>

<style lang="scss">
.user-table {
  width: 100%;
  height: 544px;
  border: 1px solid rgb(234, 237, 243);
  box-shadow: 0px 1px 0px 0px rgb(234, 237, 243);
}
.user-table__header {
  height: 44px;
  border-radius: 0px;
  box-shadow: 0px 1px 0px 0px rgb(234, 237, 243);
}
.user-table__header--text {
  color: rgb(74, 83, 97);
  font-weight: 500;
  font-size: 12px;
  &::after {
    display: inline-block;
    width: 14px;
    height: 10px;
    background: url(~@/assets/img/ic_list_up.svg) center/100% no-repeat;
    content: '';
  }
  &:hover {
    cursor: pointer;
  }
}

.user-table--user-name {
  width: 600px;
}

.user-table__row {
  border-color: rgb(234, 237, 243);
  box-shadow: 0px 1px 0px 0px rgb(234, 237, 243);
}
.user-table__text {
  color: #0b1f48;
  font-weight: 500;
  font-size: 15px;
  text-align: center;
}
.user-table__index {
  color: rgb(117, 127, 145);
  font-weight: 500;
  font-size: 15px;
  text-align: center;
}

.align-left {
  text-align: left;
}
</style>
