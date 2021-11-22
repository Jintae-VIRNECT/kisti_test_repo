/**
 * 현재 사용하고 있는 협업내 기능 정보(실시간 공유, 협업 보드, AR 공유 구분)
 */

// stream: default, pointing
// drawing: line, text
// ar: arPointing, arArea, arDrawing

/**
 * 협업내 기능 정의
 */
export const VIEW = {
  STREAM: 'stream', //실시간 공유
  DRAWING: 'drawing', //협업 보드
  AR: 'ar', //AR 공유
}

/**
 * 기능별 사용 도구 정의
 */
export const ACTION = {
  //실시간 공유
  STREAM_DEFAULT: 'default', //기본
  STREAM_POINTING: 'pointing', //포인팅

  //협업 보드
  DRAWING_LINE: 'line', //그리기 모드
  DRAWING_TEXT: 'text', //텍스트 모드
  DRAWING_LOCK: 'lock', //드로잉 락

  //AR 공유
  AR_POINTING: 'arPointing', //포인팅
  AR_AREA: 'arArea', //영역 설정
  AR_DRAWING: 'arDrawing', //드로잉
  AR_3D: 'ar3d', //3D 모델 공유
}
