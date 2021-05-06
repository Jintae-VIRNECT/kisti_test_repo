/**
 * 웹 서버 및 어플리케이션 구동 관련 환경 설정값
 */

/**
 * 클라이언트 내에서 참조되는 환경 변수
 * @default 'production'
 */
export let RUNTIME_ENV = 'production'

/**
 * REST API TIMEOUT
 * @default 5000
 */
export let TIMEOUT = 5000

/**
 * 오디오 없이 협업 가능 여부
 * @default false
 */
export let ALLOW_NO_AUDIO = false

/**
 * 오디오, 비디오 둘다 없을 때 협업 가능 여부
 * @default false
 */
export let ALLOW_NO_DEVICE = false

/**
 * 회사 구분 코드
 * @default 0
 */
export let TARGET_COMPANY = 0 //'VIRNECT'

/**
 * onpremise(구축형) 환경에서 워크스테이션에 설정된 로고 이미지 경로
 * @default false
 */
export let WHITE_LOGO = false

/**
 * onpremise(구축형) 환경에서 워크스테이션에 설정된 로고 이미지 경로.
 *
 * WHITE_LOGO가 없을시 사용됨.
 * @default false
 */
export let DEFAULT_LOGO = false

export const setConfigs = configs => {
  RUNTIME_ENV = configs.RUNTIME_ENV || RUNTIME_ENV
  TIMEOUT = configs.TIMEOUT || TIMEOUT
  ALLOW_NO_AUDIO = configs.ALLOW_NO_AUDIO || ALLOW_NO_AUDIO
  ALLOW_NO_DEVICE = configs.ALLOW_NO_DEVICE || ALLOW_NO_DEVICE
  TARGET_COMPANY = configs.targetCompany || TARGET_COMPANY
  WHITE_LOGO = configs.whiteLogo || WHITE_LOGO
  DEFAULT_LOGO = configs.defaultLogo || DEFAULT_LOGO
}

/**
 * 런타임 변수 정의
 */
export const RUNTIME = {
  LOCAL: 'local', //로컬 환경
  DEVELOP: 'develop', //개발 환경
  STAGING: 'staging', //스테이징 환경
  PRODUCTION: 'production', //프로덕션 환경
  ONPREMISE: 'onpremise', //onpremise (구축형) 환경
}

/**
 * 클라이언트에서 참조할 외부 리소스 주소 저장
 */
export let URLS = {}

/**
 * 외부 리소스 주소 셋팅
 * @param {Object} urls config server에서 참조한 주소 객체
 */
export const setUrls = urls => {
  URLS = {
    ...urls,
  }
}

/**
 * 서버 녹화 관련 정보 셋팅
 */
export let RECORD_INFO = {}
export const setRecordInfo = info => {
  RECORD_INFO = info
}

export default {
  RUNTIME_ENV,
  TARGET_COMPANY,
}
