import { dateTimeFormat, durationFormat } from 'utils/dateFormat'
import { deepGet } from 'utils/util'
import XLSX from 'xlsx'

export const exportExcel = (raws, header) => {
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
            .join(' '),
        )
      } else if (key[0] === 'activeDate') {
        row.push(dateTimeFormat(deepGet(raw, key)))
      } else if (key[0] === 'unactiveDate') {
        row.push(dateTimeFormat(deepGet(raw, key)))
      } else if (key[0] === 'durationSec') {
        row.push(durationFormat(deepGet(raw, key)))
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
  element.setAttribute('download', 'virnect_history.xlsx')
  element.click()
}

const s2ab = s => {
  const buf = new ArrayBuffer(s.length) //convert s to arrayBuffer
  const view = new Uint8Array(buf) //create uint8array as viewer
  for (let i = 0; i < s.length; i++) view[i] = s.charCodeAt(i) & 0xff //convert to octet
  return buf
}
