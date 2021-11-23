import vue from 'apps/remote/app'

const SYSTEM = {
  CREATE: 'create',
  INVITE: 'invite',
  LEAVE: 'leave',

  SHARING_START: 'sharing-start',
  SHARING_START_LEADER: 'sharing-start-leader',
  SHARING_STOP: 'sharing-stop',
  SHARING_STOP_LEADER: 'sharing-stop-leader',

  STREAM_STOP: 'stream-stop',
  STREAM_START: 'stream-start',
  STREAM_BACKGROUND: 'stream-background',

  DRAWING: 'drawing',

  AR_DENY: 'ar-deny',
  AR_UNSUPPORT: 'ar-unsupport',
  AR_START: 'ar-start',
  AR_POINTING: 'ar-pointing',
  AR_AREA: 'ar-area',
  AR_3D: 'ar-3d',

  POINTING_ALLOW: 'pointing-allow',
  POINTING_ALLOW_NOT: 'pointing-not-allow',
  RECORD_ALLOW: 'record-allow',
  RECORD_ALLOW_NOT: 'record-not-allow',

  SEND_CANCEL: 'send-cancel',

  CAMERA_CONTROL_ON: 'camera-control-on',
  CAMERA_CONTROL_OFF: 'camera-control-off',
}

export const systemClass = status => {
  switch (status) {
    case SYSTEM.CREATE:
      return 'init'
    case SYSTEM.INVITE:
    case SYSTEM.LEAVE:
      return 'people'
    case SYSTEM.SHARING_START:
    case SYSTEM.SHARING_START_LEADER:
    case SYSTEM.SHARING_STOP:
    case SYSTEM.SHARING_STOP_LEADER:
      return 'sharing'
    case SYSTEM.STREAM_STOP:
    case SYSTEM.SEND_CANCEL:
      return 'cancel'
    case SYSTEM.STREAM_START:
    case SYSTEM.STREAM_BACKGROUND:
      return 'alarm'
    case SYSTEM.DRAWING:
      return 'board'
    case SYSTEM.AR_DENY:
    case SYSTEM.AR_UNSUPPORT:
    case SYSTEM.AR_START:
    case SYSTEM.AR_POINTING:
    case SYSTEM.AR_AREA:
      return 'ar'
    case SYSTEM.AR_3D:
      return 'ar-3d'
    case SYSTEM.POINTING_ALLOW:
    case SYSTEM.POINTING_ALLOW_NOT:
      return 'pointing'
    case SYSTEM.RECORD_ALLOW:
    case SYSTEM.RECORD_ALLOW_NOT:
      return 'record'
    case SYSTEM.CAMERA_CONTROL_ON:
    case SYSTEM.CAMERA_CONTROL_OFF:
      return 'camera'
    default:
      return ''
  }
}

export const systemText = (type, name) => {
  switch (type) {
    case SYSTEM.CREATE:
      return vue.$t('service.chat_create')
    case SYSTEM.INVITE:
      return vue.$t('service.chat_invite', {
        name: name,
      })
    case SYSTEM.LEAVE:
      return vue.$t('service.chat_leave', {
        name: name,
      })
    case SYSTEM.SHARING_START:
      return vue.$t('service.chat_sharing_start', {
        name: name,
      })
    case SYSTEM.SHARING_START_LEADER:
      return vue.$t('service.chat_sharing_start_leader', {
        name: name,
      })
    case SYSTEM.SHARING_STOP:
      return vue.$t('service.chat_sharing_stop', {
        name: name,
      })
    case SYSTEM.SHARING_STOP_LEADER:
      return vue.$t('service.chat_sharing_stop_leader', {
        name: name,
      })
    case SYSTEM.STREAM_STOP:
      return vue.$t('service.chat_stream_stop', {
        name: name,
      })
    case SYSTEM.STREAM_START:
      return vue.$t('service.chat_stream_start', {
        name: name,
      })
    case SYSTEM.STREAM_BACKGROUND:
      return vue.$t('service.chat_stream_background', {
        name: name,
      })
    case SYSTEM.DRAWING:
      return vue.$t('service.chat_drawing', { name: name })
    case SYSTEM.AR_DENY:
      return vue.$t('service.chat_ar_deny')
    case SYSTEM.AR_UNSUPPORT:
      return vue.$t('service.chat_ar_unsupport')
    case SYSTEM.AR_START:
      return vue.$t('service.chat_ar_start')
    case SYSTEM.AR_POINTING:
      return vue.$t('service.chat_ar_pointing', { name: name })
    case SYSTEM.AR_AREA:
      return vue.$t('service.chat_ar_area')
    case SYSTEM.AR_3D:
      return vue.$t('service.chat_ar_3d_start')
    case SYSTEM.POINTING_ALLOW:
      return vue.$t('service.chat_pointing_allow')
    case SYSTEM.POINTING_ALLOW_NOT:
      return vue.$t('service.chat_pointing_not_allow')
    case SYSTEM.RECORD_ALLOW:
      return vue.$t('service.chat_record_allow')
    case SYSTEM.RECORD_ALLOW_NOT:
      return vue.$t('service.chat_record_not_allow')
    case SYSTEM.SEND_CANCEL:
      return vue.$t('service.chat_send_cancel', { name: name })
    case SYSTEM.CAMERA_CONTROL_ON:
      return vue.$t('service.chat_camera_control_on')
    case SYSTEM.CAMERA_CONTROL_OFF:
      return vue.$t('service.chat_camera_control_off')
  }
}
