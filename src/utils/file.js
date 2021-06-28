import axios from 'axios'
import { URLS } from 'configs/env.config'

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
export const downloadByURL = async (file, usingNewTab = false) => {
  // let a = document.createElement('a')
  // document.body.appendChild(a)
  // a.style = 'display: none'
  // a.href = file.url
  // a.download = file.name
  // a.target = '_blank'
  // a.click()
  // window.URL.revokeObjectURL(file.url)
  if (usingNewTab) {
    window.open(proxyUrl(file.url))
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
