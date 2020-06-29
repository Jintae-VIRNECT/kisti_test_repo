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
  captureFile: state => state.files.captureFile,

  // setting
  // mic: state => state.oncall.mic,
  // speaker: state => state.oncall.speaker,
  mic: state => state.settings.mic,
  speaker: state => state.settings.speaker,
  localRecord: state => state.settings.localRecordInfo,
  allow: state => state.settings.allow,

  language: state => state.settings.language,

  //screen stream for local recording
  screenStream: state => state.settings.screenStream,
  localRecordTarget: state => state.settings.localRecordTarget,
}
