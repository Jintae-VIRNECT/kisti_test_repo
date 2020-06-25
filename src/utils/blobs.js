import axios from 'axios'

/**
 * convert base64 to blob
 * @async
 * @param {String} b64Url base64 string
 * @param {String} dataType data type
 * @param {String} fileName file name
 * @return {Blob} converted blob
 */
export const base64urlToBlob = async (b64Url, dataType, fileName) => {
  const res = await axios.get(b64Url, {
    responseType: 'blob',
  })
  const blob = new File([await res.data], fileName, {
    type: dataType,
  })

  return blob
}
