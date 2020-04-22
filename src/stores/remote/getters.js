export default {
  account: state => state.account,

  // oncall
  view: state => state.oncall.view,
  action: state => state.oncall.action,
  stream: state => state.oncall.stream,
  mic: state => state.oncall.mic,
  speaker: state => state.oncall.speaker,
  mute: state => !state.oncall.unmute,

  deviceType: state => state.device.type,

  searchFilter: state => state.workspace.search.filter,

  //member
  memberListLength: state => state.workspace.memberList.length,
}
