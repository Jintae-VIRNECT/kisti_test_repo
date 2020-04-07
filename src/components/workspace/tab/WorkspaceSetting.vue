<template>
  <tab-view title="사용자 환경설정">
    <div style="height: 300px;">
      <scrollbar>
        <div style="height: 1000px;border: solid 1px #d8d8d8;">
          <div class="workspace-setting-wrapper">
            <div class="workspace-setting-title">비디오 및 오디오 설정</div>
            <workspace-set-audio
              :audioInputDevices="audioInputDevices"
              :audioOutputDevices="audioOutputDevices"
            ></workspace-set-audio>

            <div class="workspace-setting-horizon-wrapper">
              <workspace-set-video
                :videoDevices="videoDevices"
              ></workspace-set-video>

              <workspace-set-language></workspace-set-language>
            </div>
            <workspace-set-record></workspace-set-record>
            <workspace-set-notification></workspace-set-notification>
          </div>
        </div>
      </scrollbar>
    </div>
  </tab-view>
</template>
<script>
import TabView from '../partials/workspaceTabView'
import Scrollbar from 'Scroller'
import WorkspaceSetAudio from '../section/WorkspaceSetAudio'
import WorkspaceSetVideo from '../section/WorkspaceSetVideo'
import WorkspaceSetLanguage from '../section/WorkspaceSetLanguage'
import WorkspaceSetNotification from '../section/WorkspaceSetNotification'
import WorkspaceSetRecord from '../section/WorkspaceSetRecord'
export default {
  name: 'WorkspaceSetting',
  components: {
    TabView,
    Scrollbar,
    WorkspaceSetAudio,
    WorkspaceSetVideo,
    WorkspaceSetLanguage,
    WorkspaceSetNotification,
    WorkspaceSetRecord,
  },
  data() {
    return {
      //device list
      videoDevices: [],
      audioInputDevices: [],
      audioOutputDevices: [],
    }
  },
  computed: {},
  watch: {},
  methods: {
    init() {
      navigator.mediaDevices
        .enumerateDevices()
        .then(devices => {
          console.log(devices)
          devices.forEach(device => {
            if (device.kind === 'videoinput') {
              this.videoDevices.push(device)
            } else if (device.kind === 'audioinput') {
              this.audioInputDevices.push(device)
            } else if (device.kind === 'audiooutput') {
              this.audioOutputDevices.push(device)
            }
          })
        })
        .catch(err => {
          console.log(err)
        })
    },
  },

  /* Lifecycles */
  mounted() {
    this.init()
  },
}
</script>
<style scoped></style>
