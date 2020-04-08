<template>
  <tab-view title="사용자 환경설정">
    <div style="height: 400px;">
      <scrollbar>
        <div style="height: 1500px; background-color:#29292c">
          <div class="workspace-setting-wrapper">
            <workspace-set-audio
              class="workspace-setting-section"
              :audioInputDevices="audioInputDevices"
              :audioOutputDevices="audioOutputDevices"
              @selectedAudioInputDevice="setAudioInputDevice"
              @selectedOutputAudioDevice="setAudioOutputDevice"
            ></workspace-set-audio>
            <workspace-mic-test
              class="workspace-setting-section"
              :selectAudioInput="selectAudioInput"
            >
            </workspace-mic-test>

            <div
              class="workspace-setting-horizon-wrapper workspace-setting-section"
            >
              <workspace-set-video
                :videoDevices="videoDevices"
                @selectedVideoDevice="saveVideoDevice"
                @selectedVideoQuality="saveVideoQuality"
              ></workspace-set-video>

              <workspace-set-language
                @selectedLanguage="saveLanguage"
              ></workspace-set-language>
            </div>
            <div
              class="workspace-setting-horizon-wrapper workspace-setting-section"
            >
              <workspace-set-record
                @selectedRecordLength="saveRecordLength"
                @selectedRecordRes="saveRecordRes"
              ></workspace-set-record>
            </div>
            <div
              class="workspace-setting-horizon-wrapper workspace-setting-section"
            >
              <workspace-set-notification
                @selectedNotiFlagPC="saveNotiFlagPC"
              ></workspace-set-notification>
            </div>
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
import WorkspaceMicTest from '../section/WorkspaceMicTest'
import { CONFIG_CODE } from 'utils/config-codes'

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
    WorkspaceMicTest,
  },
  data() {
    return {
      //device list
      videoDevices: [],
      audioInputDevices: [],
      audioOutputDevices: [],

      //audio context
      audioContext: null,
      selectAudioInput: null,
      selectAudioOutput: null,
    }
  },
  computed: {},
  watch: {},
  methods: {
    getMediaDevice() {
      navigator.mediaDevices
        .enumerateDevices()
        .then(devices => {
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
    setAudioInputDevice(newInputAudioDevice) {
      this.selectAudioInput = newInputAudioDevice
      this.saveAudioInputDevice(newInputAudioDevice)
    },
    setAudioOutputDevice(newOutputAudioDevice) {
      this.selectAudioOutput = newOutputAudioDevice
      this.saveAudioOutputDevice(newOutputAudioDevice)
    },
    saveVideoDevice(videoDevice) {
      if (videoDevice !== null) {
        localStorage.setItem(
          CONFIG_CODE.CURRENT_VIDEO_DEVICE,
          JSON.stringify(videoDevice),
        )
      }
    },
    saveVideoQuality(videoQuality) {
      if (videoQuality !== null) {
        localStorage.setItem(
          CONFIG_CODE.CURRENT_VIDEO_QUALITY,
          JSON.stringify(videoQuality),
        )
      }
    },
    saveAudioInputDevice(audioInputDevice) {
      if (audioInputDevice !== null) {
        localStorage.setItem(
          CONFIG_CODE.CURRENT_AUDIO_INPUT_DEVICE,
          JSON.stringify(audioInputDevice),
        )
      }
    },
    saveAudioOutputDevice(audioOutputDeivce) {
      if (audioOutputDeivce !== null) {
        localStorage.setItem(
          CONFIG_CODE.CURRENT_AUDIO_OUTPUT_DEVICE,
          JSON.stringify(audioOutputDeivce),
        )
      }
    },
    saveLanguage(language) {
      if (language !== null) {
        localStorage.setItem(
          CONFIG_CODE.CURRENT_LANGUAGE,
          JSON.stringify(language),
        )
      }
    },
    saveRecordLength() {},
    saveRecordRes() {},
    saveNotiFlagPC() {},
  },

  /* Lifecycles */
  mounted() {
    this.getMediaDevice()
  },
}
</script>
<style scoped></style>
