import uuid from 'uuid'
import IDBHelper from 'utils/idbHelper'
import MSR from 'plugins/remote/msr/MediaStreamRecorder.js'

export default class LocalRecorder {
  constructor() {
    this.recorder = null
    this.timeMark = 0
    this.groupId = null
    this.fileName = ''
    this.fileCount = 0
    this.totalPlayTime = 0

    //may change if provide more options
    this.mimeType = 'video/webm;codecs=vp9'

    this.today = null
    this.options = {}
    this.roomTitle = ''
    this.maxTime = 60
    this.streams = []
    this.interval = 60
    this.nickName = 'NONE'
    this.userId = 'NONE'
  }

  setConfig(config) {
    this.today = config.today
    this.options = config.options
    this.roomTitle = config.roomTitle
    this.maxTime = config.maxTime
    this.streams = config.streams
    this.interval = config.interval
    this.nickName = config.nickName
    this.userId = config.userId
  }

  /**
   * @param {Function} startCallback
   */
  setStartCallback(startCallback) {
    this.startCallback = startCallback
  }

  /**
   * @param {Function} stopCallback
   */
  setStopCallback(stopCallback) {
    this.stopCallback = stopCallback
  }

  /**
   * @param {Function} noQuotaCallback
   */
  setNoQuotaCallback(noQuotaCallback) {
    this.noQuotaCallback = noQuotaCallback
  }

  async initRecorder() {
    //for group id
    this.groupId = uuid()

    await IDBHelper.initIDB()

    //reset fileCount
    this.fileCount = 0

    if ((await this.checkQuota()) === false) {
      return false
    }

    if (this.streams.length <= 0) {
      return false
    }

    this.recorder = new MSR.MultiStreamRecorder(this.streams, this.options)
    this.recorder.mimeType = this.mimeType
    // don't provide bit rate option yet
    // this.recorder.videoBitsPerSecond = this.bitrate
    this.recorder.ondataavailable = this.getOndataavailable()

    return true
  }
  startRecord() {
    try {
      this.isRecording = true

      const timeSlice = Number.parseInt(this.interval * 1000, 10)
      this.recorder.start(timeSlice)

      if (this.startCallback) {
        this.startCallback()
      }

      this.timeMark = performance.now()
    } catch (e) {
      console.error(e)
    }
  }

  stopRecord() {
    try {
      if (this.recorder) {
        this.recorder.stop()
        this.recorder.clearRecordedData()

        if (this.stopCallback) {
          this.stopCallback()
        }
      }
    } catch (e) {
      console.error(e)
    } finally {
      this.recorder = null
    }
  }

  getOndataavailable() {
    const ondataavailable = async blob => {
      //create private uuid for media chunk
      const privateId = uuid()

      //make file name
      const fileNumber = this.getFileNumberString(this.fileCount)
      this.fileName =
        this.today + '_' + fileNumber + '_' + this.nickName + '.mp4'

      //get media chunk play time
      const currentTime = performance.now()
      const playTime = (currentTime - this.timeMark) / 1000
      this.timeMark = currentTime

      this.totalPlayTime = this.totalPlayTime + playTime / 60

      if (!(await this.checkQuota())) {
        this.stopRecord()
        await IDBHelper.deleteGroupMediaChunk(this.groupId)
      } else {
        //insert IDB
        IDBHelper.addMediaChunk(
          this.groupId,
          privateId,
          this.fileName,
          playTime,
          blob.size,
          blob,
          this.userId,
          this.nickName,
          this.roomTitle,
        )
      }

      if (this.totalPlayTime >= this.maxTime) {
        this.stopRecord()
      }

      this.fileCount++
    }

    return ondataavailable
  }

  /**
   * get file number 0 ~ 59
   * 60 over is not exits
   */
  getFileNumberString(number) {
    let count = ''
    if (number < 10) {
      count = '0' + number
    } else {
      count = number.toString(10)
    }
    return count
  }

  async checkQuota() {
    if ((await IDBHelper.checkQuota()) === false) {
      console.log('LocalRecording :: quota over!!! cancel recording!!!')
      if (this.noQuotaCallback) {
        this.noQuotaCallback()
      }
      return false
    } else {
      return true
    }
  }

  changeCanvasOrientation(orientation) {
    if (this.recorder !== null) {
      this.recorder.changeCanvasOrientation(orientation)
    }
  }
}
