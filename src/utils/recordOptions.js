import { debug } from 'utils/logger'

const RATIO_16_9 = 1.77
const RATIO_4_3 = 1.33

export const RECORD_TARGET = {
  WORKER: 'WORKER',
  SCREEN: 'SCREEN',
}

export const LCOAL_RECORD_STAUTS = {
  START: 'START',
  STOP: 'STOP',
}

// Service Options
export const localRecTime = ['5', '10', '15', '30', '60']

export const localRecResOpt = [
  {
    value: '360p',
    text: '360p',
  },
  {
    value: '480p',
    text: '480p',
  },
  {
    value: '720p',
    text: '720p',
  },
]

export const localRecInterval = ['1', '5', '10']

/**
 *
 * get resolution object with adjust ratio
 *
 * @param {String} resolution stream resolution => 360p, 480p, 720p
 * @param {Number} inputWidth stream width
 * @param {Number} inputHeight stream height
 */
export const getWH = (resolution, inputWidth, inputHeight) => {
  debug('recordOption', resolution, inputWidth, inputHeight)

  //default 16:9 480p
  const video = {
    width: 854,
    height: 480,
  }

  let inputRatio = null

  if (inputWidth === undefined || inputHeight === undefined) {
    inputRatio = RATIO_16_9
  }

  //get ratio
  if (inputWidth > inputHeight) {
    inputRatio = inputWidth / inputHeight
  } else {
    inputRatio = inputHeight / inputWidth
  }

  inputRatio = parseFloat(inputRatio.toFixed(2))

  //4:3
  if (inputRatio === RATIO_4_3) {
    switch (resolution) {
      case '360p':
        video.width = 480
        video.height = 360
        break
      case '480p':
        video.width = 640
        video.height = 480
        break

      case '720p':
        video.width = 960
        video.height = 720
        break

      default:
        video.width = 640
        video.height = 480
        console.error('getWH :: Unknown resolution ::', resolution)
        break
    }

    //16:9 and etc...
  } else {
    switch (resolution) {
      case '360p':
        video.width = 640
        video.height = 360
        break
      case '480p':
        video.width = 854
        video.height = 480
        break

      case '720p':
        video.width = 1280
        video.height = 720
        break

      default:
        video.width = 854
        video.height = 480
        console.error('getWH :: Unknown resolution ::', resolution)
        break
    }
  }
  return video
}
