export default {
  filters: {
    publishBoolean(value) {
      if (value === 'WAIT') return '배포중지'
      else if (value === 'PUBLISH') return '배포중'
      else if (value === 'MANAGED') return '공정관리중'
    },
    countStopOfContentPublish(tableData) {
      return tableData.filter(d => d.status === 'PUBLISH').length
    },
    countAllContents(tableData) {
      return tableData.length
    },
  },
}
