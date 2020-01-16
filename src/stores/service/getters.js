export default {
  // oncall
  view: state => state.oncall.view,
  action: state => state.oncall.action,
  mic: state => state.oncall.mic,
  speaker: state => state.oncall.speaker,
  mute: state => !state.oncall.unmute
}
