import axios from 'axios'

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
