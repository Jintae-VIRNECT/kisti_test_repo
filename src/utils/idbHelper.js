import Dexie from 'dexie'

/**
 * @author ykmo-VIRNECT
 */

let db = null
const logPrefix = 'IDB :: '
const USAGE_LIMIT_PERSENTAGE = 80

async function initIDB() {
  //define db structure

  db = new Dexie('RemoteMediaChunk')

  console.log(logPrefix + 'init idb')
  db.version(1).stores({
    RemoteMediaChunk:
      '++id, groupId, uuid, fileName, playTime, fileSize,  blob, accountName',
  })
}

async function addMediaChunk(
  groupId,
  uuid,
  fileName,
  playTime,
  fileSize,
  blob,
  accountName,
) {
  console.log(logPrefix + 'addMediaChunk')

  await db.RemoteMediaChunk.add({
    groupId: groupId,
    uuid: uuid,
    fileName: fileName,
    playTime: playTime,
    fileSize: fileSize,
    blob: blob,
    accountName: accountName,
  })
}

async function getAllDataArray() {
  return await db.RemoteMediaChunk.toArray()
}

async function getMediaChunkArray(fileName) {
  console.log(fileName)

  return await db.RemoteMediaChunk.where('fileName')
    .equals(fileName)
    .toArray()
}

async function getMediaChunkArrays(uuids) {
  return await db.RemoteMediaChunk.where('uuid')
    .anyOf(uuids)
    .toArray()
}

async function deleteMediaChunk(uuids) {
  console.log('deleteMediaChunk :: ', uuids)
  await db.RemoteMediaChunk.where('uuid')
    .anyOf(uuids)
    .delete()
}

async function deleteGroupMediaChunk(groupId) {
  await db.RemoteMediaChunk.where('groupId')
    .equals(groupId)
    .delete()
}

async function checkEstimatedQuota() {
  if (navigator.storage && navigator.storage.estimate) {
    const estimation = await navigator.storage.estimate()

    const quotaUsagePersentage = (
      (estimation.usage / estimation.quota) *
      100
    ).toFixed(2)

    console.log(`Quota: ${estimation.quota}`)
    console.log(`Usage: ${estimation.usage}`)
    console.log(quotaUsagePersentage)

    if (quotaUsagePersentage < USAGE_LIMIT_PERSENTAGE) {
      return true
    } else {
      return false
    }
  } else {
    console.error('StorageManager not found')
  }
}

const IDBHelper = {
  initIDB: initIDB,
  addMediaChunk: addMediaChunk,
  getMediaChunkArray: getMediaChunkArray,
  getMediaChunkArrays: getMediaChunkArrays,
  deleteMediaChunk: deleteMediaChunk,
  getAllDataArray: getAllDataArray,
  checkEstimatedQuota: checkEstimatedQuota,
  deleteGroupMediaChunk: deleteGroupMediaChunk,
}

export default IDBHelper
