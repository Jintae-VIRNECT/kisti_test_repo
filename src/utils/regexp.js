export const link = new RegExp(/^(http(s)?|ftp|tel|mailto|\/)(:\/\/)?/)

export const email = new RegExp(/[-\d\S.+_]+@[-\d\S.+_]+\.[\S]{2,4}/)

//IE RegExp 객체 이슈로 형태 변경
export const password = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[$@$!%*#?&])[A-Za-z\d$@$!%*#?&]{6,}$/i

// export const name = new RegExp(/^[/'\w\sㄱ-ㅎㅏ-ㅣ가-힣]{2,20}$/)
export const name = new RegExp(
  /^[\d.,+_/'^-\w\sㄱ-ㅎㅏ-ㅣ가-힣一-龥ぁ-ゔ々〆〤ァ-ヴ]{2,21}$/,
)
export const nameExp = new RegExp(
  /^[\d.,+_/'^-\w\sㄱ-ㅎㅏ-ㅣ가-힣一-龥ぁ-ゔ々〆〤ァ-ヴ]{0,21}$/,
)

export const nameEmpty = new RegExp(/^[\w]{0}$/)

export const groupName = new RegExp(/^([^\<\>ㄱ-ㅣ]|[\w]){2,20}$/)

export const id = new RegExp(/^([-_a-z\d]){5,20}$/)

export const validLink = target => {
  return link.test(target)
}

export const validEmail = target => {
  return email.test(target)
}

export const validPassword = target => {
  return password.test(target)
}

export const validName = target => {
  return name.test(target) | nameEmpty.test(target)
}

export const validGroupName = target => {
  return groupName.test(target)
}

export const validId = target => {
  return id.test(target)
}

export const escapeRegExp = string => {
  return string.replace(/[.*+\-?^${}()|[\]\\]/g, '\\$&')
}

export const validJsonString = str => {
  let res
  try {
    res = JSON.parse(str)
  } catch (e) {
    return null
  }

  return res
}

export const checkSpecialCharacters = str => {
  const pattern = /[~!@#$%^&*()_+|<>?:{}]/g
  return pattern.test(str)
}
