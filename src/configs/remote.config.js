export const SIGNAL = {
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
}

export const VIDEO = {
  NORMAL: 'normal',
  SHARE: 'share',
}

export const CAMERA = {
  ZOOM: 'zoom',
  STATUS: 'status',
}

export const FLASH = {
  FLASH: 'flash',
  STATUS: 'status',
}

export const CONTROL = {
  POINTING: 'pointing',
  LOCAL_RECORD: 'localRecord',
}

export const DRAWING = {
  LINE_DOWN: 'drawLineDown',
  LINE_MOVE: 'drawLineMove',
  LINE_UP: 'drawLineUp',
  TEXT_ADD: 'drawText',
  TEXT_UPDATE: 'updateText',
  UNDO: 'drawUndo',
  REDO: 'drawRedo',
  CLEAR_ALL: 'drawClearAll',

  FIRST_FRAME: 'firstFrame',
  FRAME: 'frame',
  LAST_FRAME: 'lastFrame',
  END_DRAWING: 'endDrawing',
}

export const AR_POINTING = {
  AR_POINTING: 'arPointing',
  UNDO: 'undo',
  REDO: 'redo',
  CLEAR: 'clear',
  UNDO_ABLE: 'undoAble',
  REDO_ABLE: 'redoAble',
  CLEAR_ABLE: 'clearAble',
}

export const AR_FEATURE = {
  FEATURE: 'feature',
  START_AR_FEATURE: 'startArFeature',
  STOP_AR_FEATURE: 'stopArFeature',
}

export const AR_DRAWING = {
  FRAME_REQUEST: 'frameRequest',
  FRAME_RESPONSE: 'frameResponse',
  FIRST_FRAME: 'firstFrame',
  FRAME: 'frame',
  LAST_FRAME: 'lastFrame',

  START_DRAWING: 'startArDrawing',
  END_DRAWING: 'endArDrawing',

  LINE_DOWN: 'arDrawLineDown',
  LINE_MOVE: 'arDrawLineMove',
  LINE_UP: 'arDrawLineUp',
  UNDO: 'undo',
  REDO: 'redo',
  CLEAR: 'clear',

  UNDO_ABLE: 'undoAble',
  REDO_ABLE: 'redoAble',
  CLEAR_ABLE: 'clearAble',
}

export const CAPTURE_PERMISSION = {
  REQUEST: 'request',
  RESPONSE: 'response',
}

export const ROLE = {
  LEADER: 'LEADER',
  EXPERT: 'EXPERT',
  WORKER: 'WORKER',
  UNKNOWN: 'UNKNOWN',
}
