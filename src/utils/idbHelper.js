import Dexie from 'dexie'
import { logger, debug } from 'utils/logger'

/**
 * @author ykmo-VIRNECT
 */

let db = null
let initFlag = true
const logType = 'IDB'
const USAGE_LIMIT_PERSENTAGE = 80
const columns = [
  '++id',
  'groupId',
  'uuid',
  'fileName',
  'playTime',
  'fileSize',
  'blob',
  'userId',
  'accountName',
  'roomName',
  'sessionId',
]

async function initIDB() {
  //define db structure
  try {
    if (initFlag) {
      db = new Dexie('RemoteMediaChunk')

      /**
       * Warning!! if you change column then
       * increase version number
       */
      db.version(3).stores({
        RemoteMediaChunk: columns.join(', '),
      })
      initFlag = false

      logger(logType, 'init Success')
    }
    return true
  } catch (e) {
    console.error(logType, 'init Failed')
    console.error(e)
    return false
  }
}

/**
 * Add Record to Idb
 * @param {String} groupId media chunk group id
 * @param {String} uuid media chunk private id
 * @param {String} fileName media chunk file name
 * @param {Number} playTime media chunk play time(s)
 * @param {String} fileSize media chunk size (byte)
 * @param {Blob} blob media chunk blob
 * @param {String} userId account id
 * @param {String} accountName account nickname
 * @param {String} roomName room name
 * @param {String} sessionId sessionId
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
  sessionId,
) {
  debug(
    `groupId : ${groupId} 
    uuid : ${uuid}
    fileName: ${fileName} 
    playTime :  ${playTime}
    fileSize :  ${fileSize}
    blob :  ${blob} 
    userId : ${userId} 
    accountName :  ${accountName}
    roomName :  ${roomName}
    sessionId : ${sessionId}`,
  )

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
    sessionId: sessionId,
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
    .reverse()
    .sortBy('id')

  if (dataHandler && typeof dataHandler === 'function') {
    result = result.map(dataHandler)
  }

  debug(logType, 'getDataWithUserId', result)
  return result
}

async function getMediaChunkArrays(uuids) {
  let result = await db.RemoteMediaChunk.where('uuid')
    .anyOf(uuids)
    .toArray()

  debug(logType, 'getMediaChunkArrays', result)
  return result
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

    logger(logType, `Quota: ${estimation.quota} Byte`)
    logger(logType, `Usage: ${estimation.usage} Byte`)
    logger(logType, `UsagePersentage: ${quotaUsagePersentage}%`)

    if (quotaUsagePersentage < USAGE_LIMIT_PERSENTAGE) {
      return true
    } else {
      return false
    }
  } else {
    console.error(logType, `StorageManager not found`)
  }
}

const IDBHelper = {
  initIDB: initIDB,
  addMediaChunk: addMediaChunk,
  getMediaChunkArrays: getMediaChunkArrays,
  deleteMediaChunk: deleteMediaChunk,
  getAllDataArray: getAllDataArray,
  getDataWithUserId: getDataWithUserId,
  checkQuota: checkQuota,
  deleteGroupMediaChunk: deleteGroupMediaChunk,
}

export default IDBHelper
