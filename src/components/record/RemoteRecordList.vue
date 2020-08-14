<template>
  <div>
    <section class="record-list__header">
      <h1 class="record-list__header--title">원격 협업 내역</h1>
      <p class="record-list__header--description">
        사용자의 원격협업 내역을 확인할 수 있습니다.
      </p>
    </section>
    <search-bar></search-bar>
    <user-table></user-table>
    <!-- 테이블 -->
  </div>
</template>

<script>
import SearchBar from './RemoteSearchBar'
import UserTable from './RemoteUserTable'
export default {
  name: 'RemoteRecordList',
  components: {
    SearchBar,
    UserTable,
  },
}
</script>

<style lang="scss">
.record-list--header {
}

.record-list__header--title {
  color: rgb(11, 31, 72);
  font-weight: 500;
  font-size: 32px;
}

.record-list__header--description {
  color: rgb(11, 31, 72);
  font-weight: 500;
  font-size: 16px;
  line-height: 20px;
}
</style>
