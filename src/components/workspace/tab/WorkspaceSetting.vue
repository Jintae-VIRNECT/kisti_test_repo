<template>
  <tab-view title="">
    <div class="setting-wrapper">
      <div class="setting-nav">
        <div class="setting-nav__header">환경설정</div>
        <div
          class="setting-nav__menu"
          :class="{ active: tabview === 'audio-video' }"
          @click="tabChange('audio-video', '비디오 및 오디오 설정')"
        >
          비디오 및 오디오 설정
        </div>
        <div
          class="setting-nav__menu"
          :class="{ active: tabview === 'video-record' }"
          @click="tabChange('video-record', '영상 및 녹화 설정')"
        >
          영상 및 녹화 설정
        </div>
        <div
          class="setting-nav__menu"
          :class="{ active: tabview === 'language' }"
          @click="tabChange('language', '언어 설정')"
        >
          언어 설정
        </div>
      </div>
      <div class="setting-view" style="height: 400px;">
        <scrollbar>
          <div class="setting-view__header">{{ headerText }}</div>

          <div style="height: 100%; background-color:#29292c">
            <div class="workspace-setting-wrapper">
              <div v-if="tabview === 'audio-video'">
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
              </div>

              <div v-else-if="tabview === 'video-record'">
                <workspace-set-video
                  class="workspace-setting-section"
                  :videoDevices="videoDevices"
                  @selectedVideoDevice="saveVideoDevice"
                  @selectedVideoQuality="saveVideoQuality"
                ></workspace-set-video>

                <workspace-set-record
                  class="workspace-setting-section"
                  @selectedRecLength="saveRecordLength"
                  @selectedResolution="saveRecordRes"
                ></workspace-set-record>
              </div>
              <div v-else-if="tabview === 'language'">
                <workspace-set-language
                  style="height: 254px;"
                  class="workspace-setting-section"
                  @selectedLanguage="saveLanguage"
                ></workspace-set-language>
              </div>
            </div>
          </div>
        </scrollbar>
      </div>
    </div>
  </tab-view>
</template>
<script>
import TabView from '../partials/WorkspaceTabView'
import Scrollbar from 'Scroller'
import WorkspaceSetAudio from '../section/WorkspaceSetAudio'
import WorkspaceSetVideo from '../section/WorkspaceSetVideo'
import WorkspaceSetLanguage from '../section/WorkspaceSetLanguage'
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
    WorkspaceSetRecord,
    WorkspaceMicTest,
  },
  data() {
    return {
      tabview: 'audio-video',
      headerText: '비디오 및 오디오 설정',

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
    tabChange(view, headerText) {
      this.tabview = view
      this.headerText = headerText
    },
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
        this.$store.commit('SET_VIDEO_DEVICE', videoDevice)
        localStorage.setItem(
          CONFIG_CODE.CURRENT_VIDEO_DEVICE,
          JSON.stringify(videoDevice),
        )
      }
    },
    saveVideoQuality(videoQuality) {
      if (videoQuality !== null) {
        this.$store.commit('SET_VIDEO_QUALITY', videoQuality)
        localStorage.setItem(
          CONFIG_CODE.CURRENT_VIDEO_QUALITY,
          JSON.stringify(videoQuality),
        )
      }
    },
    saveAudioInputDevice(audioInputDevice) {
      if (audioInputDevice !== null) {
        this.$store.commit('SET_AUDIO_INPUT_DEVICE', audioInputDevice)
        localStorage.setItem(
          CONFIG_CODE.CURRENT_AUDIO_INPUT_DEVICE,
          JSON.stringify(audioInputDevice),
        )
      }
    },
    saveAudioOutputDevice(audioOutputDeivce) {
      if (audioOutputDeivce !== null) {
        this.$store.commit('SET_AUDIO_OUTPUT_DEVICE', audioOutputDeivce)
        localStorage.setItem(
          CONFIG_CODE.CURRENT_AUDIO_OUTPUT_DEVICE,
          JSON.stringify(audioOutputDeivce),
        )
      }
    },
    saveLanguage(language) {
      if (language !== null) {
        this.$store.commit('SET_LANGUAGE', language)
        localStorage.setItem(
          CONFIG_CODE.CURRENT_LANGUAGE,
          JSON.stringify(language),
        )
      }
    },
    saveRecordLength(recLength) {
      if (recLength !== null) {
        this.$store.commit('SET_LOCAL_RECORD_LENGTH', recLength)
        localStorage.setItem(
          CONFIG_CODE.CURRNET_LOCAL_RECORD_LENGTH,
          JSON.stringify(recLength),
        )
      }
    },
    saveRecordRes(recResolution) {
      if (recResolution !== null) {
        this.$store.commit('SET_RECORD_RESOLUTION', recResolution)
        localStorage.setItem(
          CONFIG_CODE.CURRENT_RECORD_RESOLUTION,
          JSON.stringify(recResolution),
        )
      }
    },
    saveNotiFlagPC(notiFlagPC) {
      if (notiFlagPC !== null) {
        this.$store.commit('SET_NOTI_FLAG_PC', notiFlagPC)
        localStorage.setItem(
          CONFIG_CODE.CURRENT_NOTI_FLAG_PC,
          JSON.stringify(notiFlagPC),
        )
      }
    },
  },

  /* Lifecycles */
  mounted() {
    this.getMediaDevice()
  },
}
</script>
<style scoped></style>
