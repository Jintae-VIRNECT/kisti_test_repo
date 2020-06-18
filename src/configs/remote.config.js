export const SIGNAL = {
  CHAT: 'signal:chat',
  RESOLUTION: 'signal:resolution',
  POINTING: 'signal:pointing',
  DRAWING: 'signal:drawing',
  APP: 'signal:app',
  CAMERA: 'signal:camera',
  FLASH: 'signal:flash',
  MIC: 'signal:mic',
  SPEAKER: 'signal:speaker',
  AR_DRAWING: 'signal:arDrawing',
  AR_POINTING: 'signal:arPointing',
  CAPTURE_PERMISSION: 'signal:screenCapturePermission',
}

export const ROLE = {
  EXPERT_LEADER: 'LEADER',
  EXPERT: 'EXPERT',
  WORKER: 'WORKER',
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

export const AR_DRAWING = {
  REQUEST_FRAME: 'requestFrame',
  RECEIVE_FRAME: 'receivedFrame',
  FRAME_RESPONSE: 'frameResponse',
  FIRST_FRAME: 'firstFrame',
  FRAME: 'frame',
  LAST_FRAME: 'lastFrame',

  START_DRAWING: 'startArDrawing',
  END_DRAWING: 'endArDrawing',

  LINE_DOWN: 'arDrawLineDown',
  LINE_MOVE: 'arDrawLineMove',
  LINE_UP: 'arDrawLineUp',
  UNDO: 'arDrawRedo',
  REDO: 'arDrawUndo',
  CLEAR_ALL: 'arDrawClearAll',
}
