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

      <div class="setting-view">
        <div class="setting-view__header">{{ headerText }}</div>

        <div class="setting-view__body">
          <div v-if="tabview === 'audio-video'">
            <workspace-set-audio
              class="setting-section"
              :audioInputDevices="audioInputDevices"
              :audioOutputDevices="audioOutputDevices"
              @selectedAudioInputDevice="setAudioInputDevice"
              @selectedOutputAudioDevice="setAudioOutputDevice"
              :defaultInputAudio="settings.mic"
              :defaultOuputAudio="settings.speaker"
            ></workspace-set-audio>

            <workspace-mic-test
              class="setting-section"
              :selectAudioInput="selectAudioInput"
            >
            </workspace-mic-test>
          </div>

          <div v-else-if="tabview === 'video-record'">
            <workspace-set-video
              class="setting-section"
              :videoDevices="videoDevices"
              @selectedVideoDevice="saveVideoDevice"
              @selectedVideoQuality="saveVideoQuality"
            ></workspace-set-video>

            <workspace-set-record
              class="setting-section"
              @selectedRecLength="saveRecordLength"
              @selectedResolution="saveRecordRes"
              :defaultRecLength="settings.recordingTime"
              :defaultRecordRec="settings.recordingResolution"
            ></workspace-set-record>
          </div>
          <div v-else-if="tabview === 'language'">
            <workspace-set-language
              style="height: 254px;"
              class="setting-section"
              @selectedLanguage="saveLanguage"
              :defaultLanguage="settings.language"
            ></workspace-set-language>
          </div>
        </div>
      </div>
    </div>
  </tab-view>
</template>
<script>
import TabView from '../partials/WorkspaceTabView'
import WorkspaceSetAudio from '../section/WorkspaceSetAudio'
import WorkspaceSetVideo from '../section/WorkspaceSetVideo'
import WorkspaceSetLanguage from '../section/WorkspaceSetLanguage'
import WorkspaceSetRecord from '../section/WorkspaceSetRecord'
import WorkspaceMicTest from '../section/WorkspaceMicTest'
import {
  getConfiguration,
  putLanguage,
  updateConfiguration,
} from 'api/workspace/settings'

export default {
  name: 'WorkspaceSetting',
  components: {
    TabView,
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
      settings: {
        speaker: '',
        mic: '',
        language: '',
        recordingTime: '', // 최대 녹화 시간
        recordingResolution: '', // 녹화 영상 해상도
        device: '',
      },
    }
  },

  computed: {},
  watch: {
    settings: {
      deep: true,
      handler() {
        console.log('settings 호출')
        this.updateSetting()
      },
    },
  },
  methods: {
    tabChange(view, headerText) {
      this.$eventBus.$emit('popover:close')
      this.$eventBus.$emit('scroll:reset')
      this.$nextTick(() => {
        this.tabview = view
        this.headerText = headerText
      })
    },
    async getPermissionWithDevice() {
      await navigator.mediaDevices
        .getUserMedia({ audio: true, video: true })
        .catch(err => {
          console.log(err.name + ': ' + err.message)
        })
      this.getMediaDevice()
    },
    checkPermission() {
      //Reserved for follow-up code when permission is denied
      navigator.permissions
        .query({ name: 'camera' })
        .then(function(permissionStatus) {
          console.log('camera : ', permissionStatus.state) // granted, denied, prompt
        })
      navigator.permissions
        .query({ name: 'microphone' })
        .then(function(permissionStatus) {
          console.log('microphone : ', permissionStatus.state)
        })
    },
    async getMediaDevice() {
      await navigator.mediaDevices
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
      this.settings.mic = newInputAudioDevice.deviceId
    },
    setAudioOutputDevice(newOutputAudioDevice) {
      this.selectAudioOutput = newOutputAudioDevice
      this.saveAudioOutputDevice(newOutputAudioDevice)
      this.settings.speaker = newOutputAudioDevice.deviceId
    },
    saveVideoDevice(videoDevice) {
      if (videoDevice !== null) {
        this.$store.commit('SET_VIDEO_DEVICE', videoDevice)
        this.settings.videoDevice = videoDevice
      }
    },
    saveVideoQuality(videoQuality) {
      if (videoQuality !== null) {
        this.$store.commit('SET_VIDEO_QUALITY', videoQuality)
      }
    },
    saveAudioInputDevice(audioInputDevice) {
      if (audioInputDevice !== null) {
        this.$store.commit('SET_AUDIO_INPUT_DEVICE', audioInputDevice)
      }
    },
    saveAudioOutputDevice(audioOutputDeivce) {
      if (audioOutputDeivce !== null) {
        this.$store.commit('SET_AUDIO_OUTPUT_DEVICE', audioOutputDeivce)
      }
    },
    saveLanguage(language) {
      if (language !== null) {
        putLanguage(language)
        this.$store.commit('SET_LANGUAGE', language)
        this.settings.language = language
      }
    },
    saveRecordLength(recLength) {
      if (recLength !== null) {
        this.$store.commit('SET_LOCAL_RECORD_LENGTH', recLength)
        this.settings.recordingTime = recLength
      }
    },
    saveRecordRes(recResolution) {
      if (recResolution !== null) {
        this.$store.commit('SET_RECORD_RESOLUTION', recResolution)
        this.settings.recordingResolution = recResolution
      }
    },
    async updateSetting() {
      await updateConfiguration(this.settings)
    },
  },

  /* Lifecycles */
  async created() {
    try {
      this.getPermissionWithDevice()
      //this.checkPermission()
      const datas = await getConfiguration()
      this.settings = datas.data
    } catch (err) {
      // Handle Error
      console.error(err)
    }
  },
  mounted() {},
}
</script>
