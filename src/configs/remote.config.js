/**
 * openvidu session signal에 사용되는 상수 값 및 행위 정의
 * @see https://www.notion.so/virnect/Remote-2-0-Session-Signal-b2f5c765a16843f8b33bbb179e677def
 */

/**
 * 시그널 유형 정의
 */
export const SIGNAL = {
  SYSTEM: 'signal:system',
  VIDEO: 'signal:video',
  CHAT: 'signal:chat',
  RESOLUTION: 'signal:resolution',
  POINTING: 'signal:pointing',
  DRAWING: 'signal:drawing',
  APP: 'signal:app',
  CAMERA: 'signal:camera',
  FLASH: 'signal:flash',
  MIC: 'signal:mic',
  SPEAKER: 'signal:speaker',
  CONTROL: 'signal:control',
  AR_FEATURE: 'signal:arFeature',
  AR_DRAWING: 'signal:arDrawing',
  AR_POINTING: 'signal:arPointing',
  CAPTURE_PERMISSION: 'signal:screenCapturePermission',
  FILE: 'signal:file',
  LINKFLOW: 'signal:linkflow',
  LOCATION: 'signal:location',
  DRAWING_FROM_VUEX: 'signal:drawingFromVuex',
  AR_3D: 'signal:3dContentsShare',
}

/**
 * 시그널 - 채팅파일 업로드
 */
export const FILE = {
  UPLOADED: 'fileUploaded',
}

/**
 * 시그널 - 메인 영상 표출 상태
 */
export const VIDEO = {
  NORMAL: 'normal', //일반
  SHARE: 'share', //전체 공유
  SCREEN_SHARE: 'screenShare', //PC 화면 공유
}

/**
 * 시그널 - 카메라 제어
 */
export const CAMERA = {
  ZOOM: 'zoom',
  STATUS: 'status',
}

/**
 * 시그널 - 플래시 제어
 */
export const FLASH = {
  FLASH: 'flash',
  STATUS: 'status',
}

/**
 * 시그널 - 참가자의 기능 제어
 */
export const CONTROL = {
  POINTING: 'pointing',
  LOCAL_RECORD: 'localRecord',
  VIDEO: 'video',
  AUDIO: 'audio',
  AUDIO_VIDEO: 'audioVideo',
  RESTRICTED_MODE: 'restrictedMode',
}

/**
 * 시그널 - 협업 보드 드로잉
 */
export const DRAWING = {
  LINE_DOWN: 'drawLineDown',
  LINE_MOVE: 'drawLineMove',
  LINE_UP: 'drawLineUp',
  TEXT_ADD: 'drawText',
  TEXT_UPDATE: 'updateText',
  UNDO: 'drawUndo',
  REDO: 'drawRedo',
  CLEAR: 'clear',
  CLEAR_ALL: 'clearAll',

  // FIRST_FRAME: 'firstFrame',
  // FRAME: 'frame',
  // LAST_FRAME: 'lastFrame',
  END_DRAWING: 'endDrawing',

  FILE_SHARE: 'fileShare',
  ADDED: 'added',
  DELETED: 'deleted',
}

/**
 * 시그널 - AR 포인팅
 */
export const AR_POINTING = {
  AR_POINTING: 'arPointing',
  UNDO: 'undo',
  REDO: 'redo',
  CLEAR: 'clear',
  UNDO_ABLE: 'undoAble',
  REDO_ABLE: 'redoAble',
  CLEAR_ABLE: 'clearAble',
}

/**
 * 시그널 - AR 기능 제어
 */
export const AR_FEATURE = {
  FEATURE: 'feature',
  START_AR_FEATURE: 'startArFeature',
  STOP_AR_FEATURE: 'stopArFeature',
}

/**
 * AR 드로잉 정의 및 시그널
 */
export const AR_DRAWING = {
  FRAME_REQUEST: 'frameRequest',
  FRAME_RESPONSE: 'frameResponse',
  FIRST_FRAME: 'firstFrame',
  FRAME: 'frame',
  LAST_FRAME: 'lastFrame',

  START_DRAWING: 'startArDrawing',
  END_DRAWING: 'endArDrawing',

  AR_DRAWING: 'arDrawing',

  UNDO: 'undo',
  REDO: 'redo',
  CLEAR: 'clear',

  UNDO_ABLE: 'undoAble',
  REDO_ABLE: 'redoAble',
  CLEAR_ABLE: 'clearAble',
}

/**
 * AR 드로잉 행위 정의
 */
export const AR_DRAWING_ACTION = {
  LINE_DOWN: 'arDrawLineDown',
  LINE_MOVE: 'arDrawLineMove',
  LINE_UP: 'arDrawLineUp',
}

/**
 * AR 3D 공유 정의
 */
export const AR_3D_CONTENT_SHARE = {
  START_SHARE: 'start3dContentsShare',
  STOP_SHARE: 'stop3dContentsShare',
  CONTENT_AUGUMENTED: '3dContentsAugmented',
  FILE_SHARE: '3dContentsFileShare',
  CLEAR_CONTENTS: '3dContentsClear',

  // ADDED: 'added', //파일 추가
  // DELETED: 'deleted', //파일 제거
}

export const AR_3D_FILE_SHARE_STATUS = {
  START: 'start',
  CANCEL: 'cancel',
  COMPLETE: 'complete',
  ERROR: 'error',
}

/**
 * 시그널 - 캡처 권한 제어
 */
export const CAPTURE_PERMISSION = {
  REQUEST: 'request',
  RESPONSE: 'response',
}

/**
 * 시그널 - 서비스 서버 메시지
 */
export const SYSTEM = {
  EVICT: 'evict',
  DELETED_ACCOUNT: 'DELETED_ACCOUNT',
}

/**
 * 참가자 역할 정의
 */
export const ROLE = {
  LEADER: 'LEADER', //리더
  EXPERT: 'EXPERT', //전문가
  WORKER: 'WORKER', //작업자
  UNKNOWN: 'UNKNOWN', //알수없음
  SECESSION: 'SECESSION', //탈퇴
  GUEST: 'GUEST',
}

/**
 * 시그널 - 위치정보 제어
 */
export const LOCATION = {
  REQUEST: 'requestLocation', //참가자에게 위치 정보 요청
  RESPONSE: 'responseLocation', //위치 요청 동의/거절 여부
  INFO: 'locationInfo', //위지 정보
  STOP_SEND: 'stopSendLocationInfo', //위치 요청을 멈출 단일 참가자

  STOPPED: 'stopped', //GPS 기능 off or 전체 화면 공유로 인한 위치 정보 공유 중단
}

export const USER_TYPE = {
  GUEST_USER: 'GUEST_USER',
}

export const FILE_TYPE = {
  SHARE: 'SHARE',
  OBJECT: 'OBJECT',
}

export const ACCEPTABLE_FILE_TYPE = {
  SHARE: [
    'image/jpeg',
    'image/png',
    'image/bmp',
    'image/gif',
    'application/pdf',
  ],
  OBJECT: ['obj' /*, 'gltf', 'glb'*/], //gltf 업로드는 추후 지원 여부 결정
}
