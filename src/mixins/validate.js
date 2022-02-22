import dayjs from 'dayjs'

export const passValidate = password => {
  if (/(.)\1\1\1/.test(password)) return false
  if (/(0123|1234|2345|3456|4567|5678|6789|7890)/.test(password)) return false
  if (/(0987|9876|8765|7654|6543|5432|4321|3210)/.test(password)) return false
  if (!/^(?=.*[.,!@#$%])[A-Za-z\d.,!@#$%]{8,20}$/g.test(password)) return false
  return true
}

export const domainRegex =
  /^(((http(s?)):\/\/)?)([0-9a-zA-Z-]+\.)+[a-zA-Z]{2,6}(:[0-9]+)?(\/\S*)?/

export const emailValidate = email => {
  if (/^[a-z0-9_+.-]+@([a-z0-9-]+\.)+[a-z0-9]{2,4}$/.test(email)) return true
  else false
}

export const nickNameValidate = nickName => {
  if (/\s/.test(nickName)) return false
  if (/[<>]/.test(nickName)) return false
  if (nickName.length > 20) return false
  return true
}

export const mobileNumberValidate = number => {
  if (/^\d{3}-?\d{3,4}-?\d{4}$/.test(number)) return true
  else return false
}

export const userBirth = ({ year, month, date }) => {
  let BIRTH,
    YEAR = dayjs(year),
    MONTH = dayjs(month + 1),
    DATE = dayjs(date)

  BIRTH = YEAR.format('YYYY-') + MONTH.format('MM-') + DATE.format('DD')
  return BIRTH
}

export const validBirth = ({ year, month, date }, birth) => {
  return year && month && date && new Date(birth) != 'Invalid Date'
}

export const validImage = (event, file) => {
  let files = event.target.files
  return new Promise((resolve, reject) => {
    if (files.length > 0) {
      if (['image/jpeg', 'image/jpg', 'image/png'].indexOf(files[0].type) < 0) {
        reject('This format is unavailable.')
        return
      }
      if (files[0].size > 2 * 1024 * 1024) {
        reject('This image size is unavailable.')
        return
      }
      file.value = null

      const oReader = new FileReader()
      oReader.onload = e => {
        const imageData = e.target.result
        const oImg = new Image()
        oImg.onload = _event => {
          resolve(imageData)
          _event.target.remove()
        }
        oImg.onerror = () => {
          //이미지 아닐 시 처리.
          reject('This image is unavailable.')
        }
        oImg.src = imageData
      }
      oReader.readAsDataURL(files[0])
    }
  })
}
