import http from 'api/gateway'
import API from '../gateway/api'

export const getRecordFiles = async function(data) {
  const returnVal = await http('RECORD_FILES', data)
  return returnVal
}

export const downloadRecordFile = async function(data) {
  // const returnVal = await http('DOWNLOAD_RECORD_FILE', data)
  let url = API['DOWNLOAD_RECORD_FILE'][1]
  const dataId = data.id
  return url + dataId
}

export const deleteRecordFile = async function(data) {
  const returnVal = await http('DELETE_RECORD_FILE', data)
  return returnVal
}
