import { dateTimeFormat, durationFormat } from 'utils/dateFormat'
import { deepGet } from 'utils/util'

export const exportExcel = (datas, header) => {
  let csvString = ''

  const headerString = header.join(',')

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
    ['serverRecord'],
    ['localRecord'],
    ['attach'],
  ]

  datas.forEach(data => {
    console.log(data)
    keys.forEach(key => {
      let value = ''
      if (key[0] === 'memberList') {
        const memberList = deepGet(data, key)
        value = memberList
          .map(member => {
            return member.nickName
          })
          .join(' ')
      } else if (key[0] === 'activeDate') {
        value = dateTimeFormat(deepGet(data, key))
      } else if (key[0] === 'unactiveDate') {
        value = dateTimeFormat(deepGet(data, key))
      } else if (key[0] === 'durationSec') {
        value = durationFormat(deepGet(data, key))
      } else {
        value = deepGet(data, key)
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
