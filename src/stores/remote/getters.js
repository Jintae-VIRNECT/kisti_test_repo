export default {
  account: state => state.account,
  workspace: state => state.workspace.current,

  // oncall
  view: state => state.oncall.view,
  tools: state => {
    return {
      color: state.oncall.drawColor,
      opacity: state.oncall.drawOpacity,
      width: state.oncall.lineWidth,
      size: state.oncall.fontSize,
    }
  },
  action: state => state.oncall.action,
  stream: state => state.oncall.stream,
  mic: state => state.oncall.mic,
  speaker: state => state.oncall.speaker,
  mute: state => !state.oncall.unmute,

  deviceType: state => state.device.type,

  searchFilter: state => state.sort.search.filter,
}
