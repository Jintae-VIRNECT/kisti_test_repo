export default {
  account: state => state.account,
  workspace: state => state.workspace.current,

  // oncall
  view: state => state.oncall.view,
  tools: state => {
    return {
      color: state.oncall.drawColor,
      opacity: state.oncall.drawOpacity,
      lineWidth: state.oncall.lineWidth,
      fontSize: state.oncall.fontSize,
    }
  },
  viewAction: state => state.oncall.action,
  stream: state => state.oncall.stream,
  mic: state => state.oncall.mic,
  speaker: state => state.oncall.speaker,
  mute: state => !state.oncall.unmute,

  deviceType: state => state.device.type,

  searchFilter: state => state.sort.search.filter,

  roomInfo: state => state.room,
  roomParticipants: state => state.room.participants,

  historyList: state => state.files.historyList,
  fileList: state => state.files.fileList,
  pdfPages: state => state.files.pdfPages,
  shareFile: state => state.files.shareFile,
  shareArImage: state => state.files.shareArImage,

  // setting
  micDevice: state => state.settings.micDevice,
  speakerDevice: state => state.settings.speakerDevice,
  language: state => state.settings.language,
  localRecordLength: state => state.settings.localRecordLength,
  recordResolution: state => state.settings.recordResolution,
  localRecordInterval: state => state.settings.localRecordInterval,
  allowPointing: state => state.settings.allowPointing,
  allowLocalRecording: state => state.settings.allowLocalRecording,

  //screen stream for local recording
  screenStream: state => state.settings.screenStream,
  localRecordTarget: state => state.settings.localRecordTarget,
}
