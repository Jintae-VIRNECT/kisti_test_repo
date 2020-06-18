import { hexToAHEX } from './color'
import { DRAWING, AR_DRAWING } from 'configs/remote.config'
import {
  normalizedPosX,
  normalizedPosY,
  originalPosX,
  originalPosY,
} from './normalize'
// tId: targetId
// oId: objectId
// aId: undolist 아이디

export const getCanvasSize = function getCanvasSize(
  containerWidth,
  containerHeight,
  imageWidth,
  imageHeight,
) {
  // containerWidth -= 156; //파일 목록 넓이

  const imageRatio = imageWidth / imageHeight
  const containerRatio = containerWidth / containerHeight
  let result = {}

  if (imageRatio > containerRatio) {
    //width 기준
    const constraint = 1 / imageRatio
    // if(imageWidth > containerWidth) {
    //     result.width = containerWidth;
    //     result.height = containerWidth * constraint;
    // } else {
    result.width = containerWidth
    result.height = containerWidth * constraint
    // }
    result.scale = result.width / imageWidth
  } else {
    //height 기준
    const constraint = imageRatio
    // if(imageHeight > containerHeight) {
    //     result.width = containerHeight * constraint;
    //     result.height = containerHeight;
    // } else {
    result.width = containerHeight * constraint
    result.height = containerHeight
    // }

    result.scale = result.height / imageHeight
  }

  return result
}

//NOT USE
export const getBackgroundOption = function getBackgroundOption(
  objectWidth,
  objectHeight,
  canvasWidth,
  canvasHeight,
) {
  const objectRatio = objectWidth / objectHeight
  const canvasRatio = canvasWidth / canvasHeight
  let tempWidth = objectWidth
  let tempHeight = objectHeight
  let scale = 1
  let result = {}

  if (objectRatio > canvasRatio) {
    //width 기준
    const constraint = 1 / objectRatio
    scale = canvasWidth / objectWidth
    if (objectWidth > canvasWidth) {
      tempWidth = canvasWidth
      tempHeight = canvasWidth * constraint
    } else {
      tempWidth = objectWidth * scale
      tempHeight = objectHeight * scale
    }
  } else {
    //height 기준
    const constraint = objectRatio
    scale = canvasHeight / objectHeight
    if (objectHeight > canvasHeight) {
      tempWidth = canvasHeight * constraint
      tempHeight = canvasHeight
    } else {
      tempWidth = objectWidth * scale
      tempHeight = objectHeight * scale
    }
  }

  result.top = parseInt((canvasHeight - tempHeight) / 2)
  result.left = parseInt((canvasWidth - tempWidth) / 2)
  result.scaleX = scale
  result.scaleY = scale

  // console.log(result);
  return result
}

export const getSignalParams = function getSignalParams(
  type,
  aId,
  object,
  status,
) {
  let params = {}
  let tId

  // if(object) tId = object.id;
  if (object) tId = object.tId

  switch (type) {
    /* 
    case 'drawMove':
      params = {
        aId,
        tId,
        posX: object.left,
        posY: object.top,
      }
      break
    case 'drawScale':
      params = {
        aId,
        tId,
        scaleX: object.scaleX / object.zoomX,
        scaleY: object.scaleY / object.zoomY,
        // posX: object.left,
        // posY: object.top
      }
      break
    case 'rotate':
      params = {
        aId,
        tId,
        degree: object.get('angle'),
      }
      break
    case 'remove':
      params = {
        aId,
        tId,
      }
      break */
    case DRAWING.LINE_DOWN:
    case AR_DRAWING.LINE_DOWN:
      params = {
        aId,
        color: hexToAHEX(status.color, status.opacity),
        width: status.width,
        posX: normalizedPosX(object.left, status.imgWidth),
        posY: normalizedPosY(object.top, status.imgHeight),
      }
      break
    case DRAWING.LINE_MOVE:
    case AR_DRAWING.LINE_MOVE:
      params = {
        aId,
        color: hexToAHEX(status.color, status.opacity),
        width: status.width,
        posX: normalizedPosX(object.left, status.imgWidth),
        posY: normalizedPosY(object.top, status.imgHeight),
      }
      break
    case DRAWING.LINE_UP:
    case AR_DRAWING.LINE_UP:
      params = {
        aId: aId - 1,
        color: hexToAHEX(status.color, status.opacity),
        width: status.width,
        posX: normalizedPosX(object.left, status.imgWidth),
        posY: normalizedPosY(object.top, status.imgHeight),
      }
      break
    case DRAWING.TEXT_ADD:
      params = {
        aId,
        text: object.text,
        color: hexToAHEX(status.color, status.opacity),
        // color: status.color,
        //TODO:: dp변환 필요
        size: status.size,
        posX: normalizedPosX(object.left, status.imgWidth),
        posY: normalizedPosY(object.top, status.imgHeight),
        width: object.width,
        height: object.height,
      }
      break
    case DRAWING.TEXT_UPDATE:
      params = {
        aId,
        tId,
        text: object.text,
        color: hexToAHEX(status.color, status.opacity),
        // color: status.color,
        //TODO:: dp변환 필요
        size: status.size,
        posX: normalizedPosX(object.left, status.imgWidth),
        posY: normalizedPosY(object.top, status.imgHeight),
        width: object.width,
        height: object.height,
      }
      break
  }

  if (!params.tId === undefined) {
    delete params.tId
  }

  //calculate scale
  // for (let key in params) {
  //   if (['width', 'height', 'size'].indexOf(key) >= 0) {
  //     // console.log(status.scale)
  //     params[key] = (params[key] * status.scale).toFixed(2)
  //   }
  //   if (key === 'size') {
  //     params[key] = params[key] * 1.25
  //   }
  // }

  return params
}

export const getReceiveParams = function getReceiveParams(type, params) {
  //calculate scale
  if (!params['scale'] || params['scale'] === 0) params['scale'] = 1
  for (let key in params) {
    if ('posX' === key) {
      params['posX'] = originalPosX(parseFloat(params['posX']), params.imgWidth)
    }
    if ('posY' === key) {
      params['posY'] = originalPosY(
        parseFloat(params['posY']),
        params.imgHeight,
      )
    }
    // if (['width', 'height', 'size'].indexOf(key) >= 0) {
    //   params[key] = parseFloat(params[key]) / params.scale
    // }
    if (key === 'size') {
      params[key] = params[key] / 1.25
    }
  }
  switch (type) {
    case DRAWING.LINE_DOWN:
      // console.log(['M', params.posX, params.posY])
      return ['M', params.posX, params.posY]
    case DRAWING.LINE_MOVE:
      // console.log(['Q', params.posX, params.posY, params.posX, params.posY])
      return ['Q', params.posX, params.posY, params.posX, params.posY]
    case DRAWING.LINE_UP:
      // console.log(['L', params.posX, params.posY])
      return ['L', params.posX, params.posY]
    // case 'drawMove':
    //   return {
    //     top: params.posY,
    //     left: params.posX,
    //   }
    case DRAWING.TEXT_ADD:
      return params
    default:
      return []
  }
}

export const calcPosition = function calcPosition(paths, width) {
  let left = -1
  let top = -1
  for (let i = 0; i < paths.length; i++) {
    if (i === 0) {
      left = paths[i][1]
      top = paths[i][2]
      continue
    }
    if (left > paths[i - 1][1]) {
      left = paths[i - 1][1]
    }
    if (top > paths[i - 1][2]) {
      top = paths[i - 1][2]
    }
  }
  top -= width / 2
  left -= width / 2
  return {
    top: top,
    left: left,
  }
}
