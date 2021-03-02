import { dateTimeFormat, durationFormat } from 'utils/dateFormat'
import { deepGet } from 'utils/util'
import XLSX from 'xlsx'

export const exportExcel = (raws, i18n, fileOptions) => {
  // 'No,협업명,협업내용,리더,참가자,시작시간,종료시간,진행시간,서버녹화,로컬녹화,첨부파일'
  const header = [
    'No',
    i18n.$t('excel.room_title'),
    i18n.$t('excel.room_description'),
    i18n.$t('excel.room_leader'),
    i18n.$t('excel.room_member_list'),
    i18n.$t('excel.room_active_date'),
    i18n.$t('excel.room_unactive_date'),
    i18n.$t('excel.room_duration_sec'),
  ]

  const keys = [
    ['no'],
    ['title'],
    ['description'],
    ['leaderNickName'],
    ['memberList'],
    ['activeDate'],
    ['unactiveDate'],
    ['durationSec'],
  ]

  if (fileOptions.allowServerRecordFileInfo) {
    header.push(i18n.$t('excel.file_server_record'))
    keys.push(['serverRecord'])
  }

  if (fileOptions.allowLocalRecordFileInfo) {
    header.push(i18n.$t('excel.file_local_record'))
    keys.push(['localRecord'])
  }

  if (fileOptions.allowAttachFileInfo) {
    header.push(i18n.$t('excel.file_attach_file'))
    keys.push(['attach'])
  }

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
