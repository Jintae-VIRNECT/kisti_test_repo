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

  /**
   * Warning!! if you change column then
   * increase version number
   */
  db.version(2).stores({
    RemoteMediaChunk:
      '++id, groupId, uuid, fileName, playTime, fileSize,  blob, userId, accountName, roomName',
  })
}

/**
 * Add Record to Idb
 * @param {*} groupId media chunk group id
 * @param {*} uuid media chunk private id
 * @param {*} fileName media chunk file name
 * @param {*} playTime media chunk play time(s)
 * @param {*} fileSize media chunk size (byte)
 * @param {*} blob media chunk blob
 * @param {*} userId account id
 * @param {*} accountName account nickname
 * @param {*} roomName room name
 */
async function addMediaChunk(
  groupId,
  uuid,
  fileName,
  playTime,
  fileSize,
  blob,
  userId,
  accountName,
  roomName,
) {
  await db.RemoteMediaChunk.add({
    groupId: groupId,
    uuid: uuid,
    fileName: fileName,
    playTime: playTime,
    fileSize: fileSize,
    blob: blob,
    userId: userId,
    accountName: accountName,
    roomName: roomName,
  })
}

/**
 * Get All Data from Idb
 */
async function getAllDataArray() {
  return await db.RemoteMediaChunk.toArray()
}

/**
 * Get data from idb with user id and excute dataHandler if exist
 * @param {String} userId account id
 * @param {Function} dataHandler data process function
 */
async function getDataWithUserId(userId, dataHandler) {
  let result = await db.RemoteMediaChunk.where('userId')
    .equals(userId)
    .toArray()

  if (dataHandler && typeof dataHandler === Function) {
    result = result.map(dataHandler)
  }

  return result
}

async function getMediaChunkArray(fileName) {
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
  await db.RemoteMediaChunk.where('uuid')
    .anyOf(uuids)
    .delete()
}

async function deleteGroupMediaChunk(groupId) {
  await db.RemoteMediaChunk.where('groupId')
    .equals(groupId)
    .delete()
}

async function checkQuota() {
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
  getDataWithUserId: getDataWithUserId,
  checkQuota: checkQuota,
  deleteGroupMediaChunk: deleteGroupMediaChunk,
}

export default IDBHelper
