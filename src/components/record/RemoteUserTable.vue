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
        <th><p class="user-table__header--text align-left">협업 이름</p></th>
        <th><p class="user-table__header--text">협업시작일</p></th>
        <th><p class="user-table__header--text">녹화</p></th>
      </tr>
    </thead>
    <tbody>
      <tr
        class="user-table__row"
        v-for="(userItem, index) in userList"
        :key="index"
      >
        <td class="user-table__text index">
          <p>{{ userItem.index }}</p>
        </td>
        <td class="user-table__text user-name">
          <p>{{ userItem.userName }}</p>
        </td>
        <td class="user-table__text cooperation-name">
          <p>{{ userItem.cooperateName }}</p>
        </td>
        <td class="user-table__text">
          {{ userItem.startDate }}
        </td>
        <td>
          <record-count-button
            :count="userItem.recordCount"
            :serialNum="userItem.serialNum"
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
    userList: {
      type: Array,
      default: () => [],
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
  // &::after {
  //   display: inline-block;
  //   width: 14px;
  //   height: 10px;
  //   background: url(~@/assets/img/ic_list_up.svg) center/100% no-repeat;
  //   content: '';
  // }
  // &:hover {
  //   cursor: pointer;
  // }
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

  &.index {
    color: rgb(117, 127, 145);
    font-weight: 500;
    font-size: 15px;
    text-align: center;
  }

  &.user-name {
    width: 201px;
    text-align: left;

    & > p {
      width: 154px;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }

  &.cooperation-name {
    width: 369px;
    text-align: left;

    & > p {
      width: 319px;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }
}
.align-left {
  text-align: left;
}
</style>
