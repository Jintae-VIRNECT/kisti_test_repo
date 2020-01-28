export default {
  filters: {
    publishBoolean(value) {
      if (value === 'stop') return '배포중지'
      else if (value === 'publishing') return '배포중'
      else if (value === 'managing') return '공정관리중'
    },
    countStopOfContentPublish(tableData) {
      return tableData.filter(d => d.contentPublish === 'publishing').length
    },
    countAllContents(tableData) {
      return tableData.length
    },
  },
}
