import uuid from 'uuid'
import IDBHelper from 'utils/idbHelper'
import { logger, debug } from 'utils/logger'
import MSR from 'plugins/remote/msr/MediaStreamRecorder.js'

const logType = 'LocalRecorder(util)'

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
    this.interval = 1
    this.nickname = 'NONE'
    this.userId = 'NONE'
    this.sessionId = ''
  }

  setConfig(config) {
    debug(logType, config)
    this.today = config.today
    this.options = config.options
    this.roomTitle = config.roomTitle
    this.maxTime = config.maxTime
    this.streams = config.streams
    this.interval = config.interval
    this.nickname = config.nickname
    this.userId = config.userId
    this.sessionId = config.sessionId
  }

  /**
   * @param {Function} startCallback
   */
  setStartCallback(startCallback) {
    this.startCallback = startCallback
  }

  /**
   * @param {Function} stopSignal
   */
  setStopSignal(stopSignal) {
    this.stopSignal = stopSignal
  }

  /**
   * @param {Function} noQuotaCallback
   */
  setNoQuotaCallback(noQuotaCallback) {
    this.noQuotaCallback = noQuotaCallback
  }

  async initRecorder() {
    if (!(await IDBHelper.initIDB())) {
      return false
    }

    if ((await this.checkQuota()) === false) {
      return false
    }

    if (this.streams.length <= 0) {
      return false
    }

    //for group id
    this.groupId = uuid()

    //reset fileCount
    this.fileCount = 0

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

      const timeSlice = Number.parseInt(this.interval * 60 * 1000, 10)
      this.recorder.start(timeSlice)

      if (this.startCallback) {
        this.startCallback()
      }

      this.timeMark = performance.now()

      logger(logType, 'start local record')
    } catch (e) {
      console.error(e)
    }
  }

  stopRecord() {
    try {
      if (this.recorder) {
        this.recorder.stop()
        this.recorder.clearRecordedData()

        logger(logType, 'stop local record')
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
        this.today + '_' + fileNumber + '_' + this.nickname + '.mp4'

      //get media chunk play time
      const currentTime = performance.now()
      const playTime = (currentTime - this.timeMark) / 1000
      this.timeMark = currentTime

      this.totalPlayTime = this.totalPlayTime + playTime / 60

      if (!(await this.checkQuota())) {
        this.stopSignal()
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
          this.nickname,
          this.roomTitle,
          this.sessionId,
        )
      }

      if (this.maxTime === null) {
        debug(logType, 'this.maxTime is null')
        this.maxTime = 120
      }

      if (this.totalPlayTime >= this.maxTime) {
        this.stopSignal()
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
      logger(logType, 'quota overed cancel recording')
      if (this.noQuotaCallback) {
        this.noQuotaCallback()
      }
      return false
    } else {
      return true
    }
  }

  changeVideoStream(videoStream, resolution) {
    this.recorder.resetVideoStreams(videoStream, resolution)
  }

  changeCanvasOrientation(orientation) {
    if (this.recorder !== null) {
      this.recorder.changeCanvasOrientation(orientation)
    }
  }
}
