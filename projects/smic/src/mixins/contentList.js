export default {
  filters: {
    publishBoolean(value) {
      if (value === 'WAIT') return '공정 미등록'
      else if (value === 'PUBLISH') return '공정 미등록'
      else if (value === 'MANAGED') return '공정 등록'
    },
    countStopOfContentPublish(tableData) {
      return tableData.filter(d => d.status === 'PUBLISH').length
    },
    countAllContents(tableData) {
      return tableData.length
    },
  },
}
