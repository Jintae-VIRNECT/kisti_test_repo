import axios from 'axios'
import { URLS } from 'configs/env.config'
import loadImage from 'blueimp-load-image'

/**
 * convert base64 to blob
 * @async
 * @param {String} b64Url base64 string
 * @param {String} dataType data type
 * @param {String} fileName file name
 * @return {Blob} converted blob
 */
export const base64ToBlob = async (b64Url, dataType, fileName) => {
  const res = await axios.get(b64Url, {
    responseType: 'blob',
  })
  const blob = new File([await res.data], fileName, {
    type: dataType,
  })

  return blob
}

export const blobToDataURL = blob => {
  return new Promise(resolve => {
    const reader = new FileReader()
    reader.onload = e => {
      resolve(e.target.result)
    }
    reader.readAsDataURL(blob)
  })
}

export const downloadByDataURL = async (url, fileName) => {
  const dataUrl = await blobToDataURL(url)
  let a = document.createElement('a')
  document.body.appendChild(a)
  a.style = 'display: none'
  a.href = dataUrl
  a.download = fileName
  a.click()
  window.URL.revokeObjectURL(dataUrl)
}
export const getFile = url => {
  return new Promise(resolve => {
    var xhr = new XMLHttpRequest()

    xhr.addEventListener('load', function() {
      if (xhr.status == 200) {
        resolve(xhr.response)
      }
    })

    xhr.open('GET', proxyUrl(url))
    xhr.responseType = 'blob'
    xhr.send(null)
  })
}

/**
 * 파일을 url로부터 다운로드 받는 함수
 * @param {Object} file 다운로드 받을 파일 정보
 * @param {Boolean} usingNewTab 새 탭을 열어서 파일을 다운로드
 */
export const downloadByURL = (file, usingNewTab = false) => {
  if (usingNewTab) {
    const result = window.open(proxyUrl(file.url))
    //@TODO result가 null이면 새로운 창을 호출하지 못한 상황임. 안내 메시지 필요함.
    if (result === null) {
      throw 'popup_blocked'
    }
  } else {
    let filename = file.name
    let xhr = new XMLHttpRequest()
    xhr.responseType = 'blob'
    xhr.onload = () => {
      // console.log(xhr.response)
      let a = document.createElement('a')
      a.href = window.URL.createObjectURL(xhr.response)
      a.download = filename
      a.style.display = 'none'
      document.body.appendChild(a)
      a.click()
      window.URL.revokeObjectURL(xhr.response)
    }
    xhr.open('GET', proxyUrl(file.url))
    xhr.send()
  }
}

export const proxyUrl = url => {
  if ('minio' in URLS) {
    return url.replace(/^((http[s]?|ftp):\/\/)([^/]+)/, URLS['minio'])
  } else {
    return url
  }
}

// 모바일 촬영 이미지의 exif 회전 값을 리셋하여 회전되있는 사진 원위치하는 함수
export const resetOrientation = async file => {
  return new Promise(res => {
    loadImage(
      file,
      (img, data) => {
        if (data && data.imageHead && data.exif) {
          //loadImage.writeExifData(data.imageHead, data, 'Orientation', 1)
          img.toBlob(blob => {
            if (!blob) res()
            const newFile = new File([blob], file.name, {
              lastModified: new Date(),
            })
            res(newFile)
            // loadImage.replaceHead(blob, data.imageHead, newBlob => {
            //   const newFile = new File([newBlob], file.name, {
            //     lastModified: new Date(),
            //   })
            //   res(newFile)
            // })
          }, file.type)
        } else res()
      },
      {
        canvas: true,
        orientation: true,
        meta: true,
      },
    )
  })
}

/**
 * 파일 사이즈를 MB표기로 반환
 *
 * 0.05MB 이하는 모두 0.1MB로 표기
 *
 * @param {Number} size 파일 사이즈(byte)
 * @returns {String} MB 표기로 변환된 파일 사이즈
 */
export const fileSizeFilter = size => {
  const fiftyKB = 51200 //50KB
  const dividedByMB = size / 1024 / 1024

  if (size <= fiftyKB) {
    return `0.1MB`
  } else {
    return `${dividedByMB.toFixed(1)}MB`
  }
}
