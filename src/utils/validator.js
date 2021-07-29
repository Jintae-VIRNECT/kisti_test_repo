import axios from 'api/axios'
import {
  RUNTIME,
  CONF_SPOT_CONTROL_ACTIVE,
  CONF_RUNTIME,
} from 'configs/env.config'

export const checkConfigurationValue = async keys => {
  const res = await axios.get(
    `${location.origin}/configs?origin=${location.hostname}`,
  )

  let result = {}

  if (keys.length) {
    keys.forEach(key => {
      if (res.data && res.data[key] !== null) result[key] = res.data[key]
      else result[key] = null
    })
    return result
  }
  return null
}

export const spotControlRouterGuard = async next => {
  const res = await checkConfigurationValue([
    CONF_SPOT_CONTROL_ACTIVE,
    CONF_RUNTIME,
  ])

  // @TO-KNOW : 현재 편의상 리더 외에도 URL 직접 페이지 접근은 가능하다.
  //구축형 && spot 기능 활성화되 있을 시 진입 가능
  if (RUNTIME.ONPREMISE === res[CONF_RUNTIME] && res[CONF_SPOT_CONTROL_ACTIVE])
    next()
  else next('/') //workspace 페이지로 이동
}
