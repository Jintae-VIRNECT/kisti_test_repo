import Dexie from 'dexie'

const db = new Dexie('RemoteMediaChunk')
const logPrefix = 'IDB :: '

async function initIDB() {
  //define db structure

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

async function deleteMediaChunk(fileNames) {
  await db.RemoteMediaChunk.where('fileName')
    .anyOf(fileNames)
    .delete()
}

const IDBHelper = {
  initIDB: initIDB,
  addMediaChunk: addMediaChunk,
  getMediaChunkArray: getMediaChunkArray,
  deleteMediaChunk: deleteMediaChunk,
  getAllDataArray: getAllDataArray,
}

export default IDBHelper
