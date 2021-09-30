import axios from 'api/axios'
import {
  RUNTIME,
  CONF_SPOT_CONTROL_ACTIVE,
  CONF_RUNTIME,
} from 'configs/env.config'

/**
 * 컨피그 서버의 설정값들을 가져와, 파라미터로 넘겨받은 키값에 해당 하는 값들만 모아 리턴하는 함수
 * @param {*} keys
 * @returns { key : value, key2 : value}
 */
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

/**
 * spot 관련 기능 페이지의 진입 가능 여부 체크 하는 함수
 * @param {Function} next router 함수 next를 파라미터로 넘겨 받음
 */
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
