import { dateTimeFormat, durationFormat } from 'utils/dateFormat'

export const exportExcel = datas => {
  const deepGet = (obj, keys) =>
    keys.reduce((xs, x) => {
      return xs && xs[x] !== null && xs[x] !== undefined ? xs[x] : null
    }, obj)

  let csvString = ''

  const headerString =
    'No,협업명,협업내용,리더,참가자,시작시간,종료시간,진행시간,서버녹화,로컬녹화,첨부파일'

  csvString = csvString + headerString + '\r\n'

  const keys = [
    ['index'],
    ['title'],
    ['description'],
    ['leader', 'nickName'],
    ['memberList'],
    ['activeDate'],
    ['unactiveDate'],
    ['durationSec'],
    ['serverRecord', 'length'],
    ['localRecord', 'length'],
    ['files', 'length'],
  ]

  datas.forEach(data => {
    keys.forEach(key => {
      let value = ''
      if (key[0] === 'memberList') {
        const memberList = deepGet(data.history, key)
        value = memberList
          .map(member => {
            return member.nickName
          })
          .join(' ')
      } else if (key[0] === 'activeDate') {
        value = dateTimeFormat(deepGet(data.history, key))
      } else if (key[0] === 'unactiveDate') {
        value = dateTimeFormat(deepGet(data.history, key))
      } else if (key[0] === 'durationSec') {
        value = durationFormat(deepGet(data.history, key))
      } else {
        value = deepGet(data.history, key)
      }
      csvString += value + ','
    })
    csvString += '\r\n'
  })

  const element = document.createElement('a')
  const blob = new Blob(['\ufeff' + csvString], {
    type: 'text/csv;charset=utf-8;',
  })

  const url = URL.createObjectURL(blob)
  element.href = url
  element.setAttribute('download', 'virnect_history.csv')
  element.click()
}
