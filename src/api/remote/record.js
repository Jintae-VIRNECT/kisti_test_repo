import http from 'api/gateway'

export const getRecordFiles = async function(data) {
  const returnVal = await http('RECORD_FILES', data)
  return returnVal
}

export const downloadRecordFile = async function(data) {
  const returnVal = await http('DOWNLOAD_RECORD_FILE', data)

  return returnVal
}

export const deleteRecordFile = async function(data) {
  const returnVal = await http('DELETE_RECORD_FILE', data)
  return returnVal
}
