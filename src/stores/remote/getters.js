export default {
  account: state => state.account,

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

  //local record
  localRecordTarget: state => state.settings.localRecordTarget,
  localRecordStatus: state => state.settings.localRecordStatus,
  serverRecordStatus: state => state.settings.serverRecordStatus,
}
