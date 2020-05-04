<template>
  <div class="tab-view">
    <div class="setting-wrapper offsetwidth">
      <div class="setting-nav">
        <div class="setting-nav__header">환경설정</div>
        <div
          class="setting-nav__menu"
          :class="{ active: tabview === 'audio-video' }"
          @click="tabChange('audio-video', '오디오 설정')"
        >
          오디오 설정
        </div>

        <div
          class="setting-nav__menu"
          :class="{ active: tabview === 'video-record' }"
          @click="tabChange('video-record', '녹화 설정')"
        >
          녹화 설정
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
          <template v-if="tabview === 'audio-video'">
            <workspace-set-audio
              class="setting-section"
              :mics="mics"
              :speakers="speakers"
            ></workspace-set-audio>

            <workspace-mic-test class="setting-section"> </workspace-mic-test>
          </template>

          <template v-else-if="tabview === 'video-record'">
            <!-- <workspace-set-video
            class="setting-section"
            :videos="videos"
            @setVideo="setVideo"
          ></workspace-set-video> -->

            <workspace-set-record
              class="setting-section"
            ></workspace-set-record>
          </template>
          <template v-else-if="tabview === 'language'">
            <workspace-set-language
              style="height: 254px;"
              class="setting-section"
            ></workspace-set-language>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import WorkspaceSetAudio from '../section/WorkspaceSetAudio'
import WorkspaceSetLanguage from '../section/WorkspaceSetLanguage'
import WorkspaceSetRecord from '../section/WorkspaceSetRecord'
import WorkspaceMicTest from '../section/WorkspaceMicTest'
//import WorkspaceSetVideo from '../section/WorkspaceSetVideo'
import { mapState } from 'vuex'
import {
  getConfiguration,
  putLanguage,
  updateConfiguration,
} from 'api/workspace/settings'

export default {
  name: 'WorkspaceSetting',
  components: {
    WorkspaceSetAudio,
    WorkspaceSetLanguage,
    WorkspaceSetRecord,
    WorkspaceMicTest,
    //WorkspaceSetVideo,
  },
  data() {
    return {
      tabview: 'audio-video',
      headerText: '오디오 설정',

      //device list
      videos: [],
      mics: [],
      speakers: [],

      //settings
      settings: {
        speaker: '',
        mic: '',
        language: '',
        recordingTime: '',
        recordingResolution: '',
        device: '',
      },

      dataReady: false,
    }
  },

  computed: {
    ...mapState({
      mic: state => state.settings.mic,
      speaker: state => state.settings.speaker,
      localRecordLength: state => state.settings.localRecordLength,
      recordResolution: state => state.settings.recordResolution,
      language: state => state.settings.language,
    }),
  },
  watch: {
    settings: {
      deep: true,
      handler() {
        this.updateSetting()
      },
    },
    mic(mic) {
      this.settings.mic = mic
    },
    speaker(speaker) {
      this.settings.speaker = speaker
    },
    localRecordLength(recordingTime) {
      this.settings.recordingTime = recordingTime
    },
    recordResolution(recordResolution) {
      this.settings.recordingResolution = recordResolution
    },
    language(language) {
      this.settings.language = language
      putLanguage(language)
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
    async getPermission() {
      try {
        const result = await Promise.all([
          navigator.permissions.query({ name: 'camera' }),
          navigator.permissions.query({ name: 'microphone' }),
        ])

        const [cameraState, micState] = result

        if (cameraState.state === 'denied' || micState.state === 'denied') {
          console.log('device access deined')
          return false
        }

        if (cameraState.state === 'prompt' || micState.state === 'prompt') {
          await navigator.mediaDevices.getUserMedia({
            audio: true,
            video: true,
          })
        }
        return true
      } catch (err) {
        console.log(err)
      }
    },
    async getMediaDevice() {
      try {
        if (
          !navigator.mediaDevices ||
          !navigator.mediaDevices.enumerateDevices
        ) {
          console.log('enumerateDevices() is not supported')
          return
        }

        const devices = await navigator.mediaDevices.enumerateDevices()
        devices.forEach(device => {
          if (device.kind === 'videoinput') {
            this.videos.push(device)
          } else if (device.kind === 'audioinput') {
            this.mics.push(device)
          } else if (device.kind === 'audiooutput') {
            this.speakers.push(device)
          }
        })
      } catch (err) {
        console.log(err)
      }
    },
    async updateSetting() {
      await updateConfiguration(this.settings)
    },
  },

  /* Lifecycles */
  async created() {
    try {
      const permission = await this.getPermission()

      if (permission) {
        await this.getMediaDevice()
      } else {
        //Show deined status and guide modal
      }

      const datas = await getConfiguration()
      this.$store.dispatch('setLanguage', datas.data.language)
      this.$store.dispatch('setMic', datas.data.mic)
      this.$store.dispatch('setSpeaker', datas.data.speaker)
      this.$store.dispatch('setLocalRecordLength', datas.data.recordingTime)
      this.$store.dispatch(
        'setRecordResolution',
        datas.data.recordingResolution,
      )
    } catch (err) {
      // Handle Error
      console.error(err)
    }
  },
  mounted() {},
}
</script>
