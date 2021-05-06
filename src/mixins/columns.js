export default {
  mounted() {
    // 사파리 테이블 버그
    document
      .querySelectorAll('.el-table__body')
      .forEach(table => (table.style.tableLayout = 'auto'))
    setTimeout(() => {
      document
        .querySelectorAll('.el-table__body')
        .forEach(table => (table.style.tableLayout = 'fixed'))
    }, 10)
  },
}
