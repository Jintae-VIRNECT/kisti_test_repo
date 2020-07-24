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

  historyList: state => state.files.historyList,
  fileList: state => state.files.fileList,
  pdfPages: state => state.files.pdfPages,
  shareFile: state => state.files.shareFile,
  shareArImage: state => state.files.shareArImage,
  captureFile: state => state.files.captureFile,

  //local record
  localRecordTarget: state => state.settings.localRecordTarget,
  localRecordStatus: state => state.settings.localRecordStatus,
}
