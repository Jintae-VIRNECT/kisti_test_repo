import MSR from 'plugins/remote/msr/MediaStreamRecorder.js'

const logType = 'LocalRecorder(util)'

//@TODO : 검증코드 작성하기 + 모듈
//@TODO : 지원하지 않는 브라우저에 대한 예외처리

export default class LocalRecorder {
  constructor() {
    this.recorder = null

    //may change if provide more options
    this.mimeType = 'video/webm;codecs=vp9'
    this.options = {}
    this.streams = []
    this.interval = 1
  }

  setConfig(config) {
    //검증이 쉬펄 하나도 없냐 ㅇ? 값에대한 벨리데이션이 하나도 없냐고 미친놈아
    // debug(logType, config)
    console.log(logType, config)
    this.options = config.options
    this.streams = config.streams
    this.interval = config.interval
  }

  /**
   * init recorder
   * @throws {String} 'no streams'
   */
  async initRecorder() {
    if (this.streams.length <= 0) {
      throw 'no streams'
    }

    this.recorder = new MSR.MultiStreamRecorder(this.streams, this.options)
    this.recorder.mimeType = this.mimeType
    // don't provide bit rate option yet
    // this.recorder.videoBitsPerSecond = this.bitrate

    return true
  }
  startRecord() {
    try {
      const timeSlice = Number.parseInt(this.interval * 60 * 1000, 10)
      this.recorder.start(timeSlice)

      console.log(logType, 'start local record')
    } catch (e) {
      console.error(e)
      throw e
    }
  }

  stopRecord() {
    try {
      if (this.recorder) {
        this.recorder.stop()
        this.recorder.clearRecordedData()

        console.log(logType, 'stop local record')
      }
    } catch (e) {
      console.error(e)
    } finally {
      this.recorder = null
    }
  }

  setOndataAvailableCallBack(callback) {
    this.recorder.ondataavailable = callback
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
