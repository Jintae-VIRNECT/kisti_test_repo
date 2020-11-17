import axios from 'axios'
import { URLS } from 'configs/env.config'

export const ALLOW_MINE_TYPE = [
  /* MS Office */
  'application/msword', // .doc, .dot
  'application/vnd.openxmlformats-officedocument.wordprocessingml.document', // docx
  'application/vnd.openxmlformats-officedocument.wordprocessingml.template', // .dotx
  'application/vnd.ms-word.document.macroEnabled.12', // .docm
  'application/vnd.ms-word.template.macroEnabled.12', // .dotm
  'application/vnd.ms-excel', // .xls, .xlt, .xla
  'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', // .xlsx
  'application/vnd.openxmlformats-officedocument.spreadsheetml.template', // .xltx
  'application/vnd.ms-excel.sheet.macroEnabled.12', // .xlsm
  'application/vnd.ms-excel.template.macroEnabled.12', // .xltm
  'application/vnd.ms-excel.addin.macroEnabled.12', // .xlam
  'application/vnd.ms-excel.sheet.binary.macroEnabled.12', // .xlsb
  'application/vnd.ms-powerpoint', // .ppt, .pot, .pps, .ppa
  'application/vnd.openxmlformats-officedocument.presentationml.presentation', // .pptx
  'application/vnd.openxmlformats-officedocument.presentationml.template', // .potx
  'application/vnd.openxmlformats-officedocument.presentationml.slideshow', // .ppsx
  'application/vnd.ms-powerpoint.addin.macroEnabled.12', // .ppam
  'application/vnd.ms-powerpoint.presentation.macroEnabled.12', // .pptm
  'application/vnd.ms-powerpoint.template.macroEnabled.12', // .potm
  'application/vnd.ms-powerpoint.slideshow.macroEnabled.12', // .ppsm
  /* HWP */
  'application/x-hwp',
  'application/haansofthwp',
  'application/vnd.hancom.hwp', // .hwp
  'application/x-hwt',
  'application/haansofthwt',
  'application/vnd.hancom.hwt', // hwt
  'application/vnd.hancom.hml',
  'application/haansofthml', // hml, .hwpml
  'application/vnd.hancom.hwpx', // .hwpx
  /* VIDEO */
  'video/webm',
  'video/ogg',
  /* AUDIO */
  'audio/midi',
  'audio/mpeg',
  'audio/webm',
  'audio/ogg',
  'audio/wav',
  'audio/wave',
  'audio/x-wav',
  'audio/x-pn-wav',
  /* IMAGE */
  'image/gif',
  'image/png',
  'image/jpeg',
  'image/bmp',
  'image/webp',
  'image/svg+xml',
  /* ETC */
  'application/pdf',
]

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

export const downloadByURL = async file => {
  // let a = document.createElement('a')
  // document.body.appendChild(a)
  // a.style = 'display: none'
  // a.href = file.url
  // a.download = file.name
  // a.target = '_blank'
  // a.click()
  // window.URL.revokeObjectURL(file.url)
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

export const proxyUrl = url => {
  if ('minio' in URLS) {
    return url.replace(/^((http[s]?|ftp):\/\/)([^/]+)/, URLS['minio'])
  } else {
    return url
  }
}
