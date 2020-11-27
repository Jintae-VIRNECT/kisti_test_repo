import { dateTimeFormat, durationFormat } from 'utils/dateFormat'
import { deepGet } from 'utils/util'
import XLSX from 'xlsx'

export const exportExcel = (raws, header) => {
  const keys = [
    ['no'],
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

  const rows = raws.map(raw => {
    const row = []
    keys.forEach(key => {
      if (key[0] === 'memberList') {
        const memberList = deepGet(raw, key)
        row.push(
          memberList
            .map(member => {
              return member.nickName
            })
            .join(','),
        )
      } else if (key[0] === 'activeDate') {
        const activeDate = deepGet(raw, key)
        activeDate ? row.push(dateTimeFormat(activeDate)) : row.push('')
      } else if (key[0] === 'unactiveDate') {
        const unactiveDate = deepGet(raw, key)
        unactiveDate ? row.push(dateTimeFormat(unactiveDate)) : row.push('')
      } else if (key[0] === 'durationSec') {
        const durationSec = deepGet(raw, key)
        durationSec ? row.push(durationFormat(deepGet(raw, key))) : row.push('')
      } else {
        row.push(deepGet(raw, key))
      }
    })
    return row
  })

  const excelDatas = [header].concat(rows)

  const wb = XLSX.utils.book_new()
  const newWorksheet = XLSX.utils.aoa_to_sheet(excelDatas)

  XLSX.utils.book_append_sheet(wb, newWorksheet, 'list')
  const wbout = XLSX.write(wb, { bookType: 'xlsx', type: 'binary' })

  const element = document.createElement('a')
  const blob = new Blob([s2ab(wbout)], {
    type: 'application/octet-stream',
  })

  const url = URL.createObjectURL(blob)
  element.href = url
  element.setAttribute('download', 'history.xlsx')
  element.click()
}

const s2ab = s => {
  const buf = new ArrayBuffer(s.length) //convert s to arrayBuffer
  const view = new Uint8Array(buf) //create uint8array as viewer
  for (let i = 0; i < s.length; i++) view[i] = s.charCodeAt(i) & 0xff //convert to octet
  return buf
}
