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
          <device-denied :visible.sync="showDenied"></device-denied>

          <template v-if="tabview === 'audio-video'">
            <set-audio
              :micDevices="micDevices"
              :speakerDevices="speakerDevices"
            ></set-audio>

            <mic-test> </mic-test>
          </template>

          <template v-else-if="tabview === 'video-record'">
            <!-- <set-video
            :videos="videoDevices"
            @setVideo="setVideo"
          ></set-video> -->

            <set-record></set-record>
          </template>
          <template v-else-if="tabview === 'language'">
            <set-language></set-language>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import SetAudio from '../section/WorkspaceSetAudio'
import SetLanguage from '../section/WorkspaceSetLanguage'
import SetRecord from '../section/WorkspaceSetRecord'
import MicTest from '../section/WorkspaceMicTest'
//import SetVideo from '../section/WorkspaceSetVideo'
import DeviceDenied from 'components/workspace/modal/WorkspaceDeviceDenied'

export default {
  name: 'WorkspaceSetting',
  components: {
    SetAudio,
    SetLanguage,
    SetRecord,
    MicTest,
    //SetVideo,
    DeviceDenied,
  },
  data() {
    return {
      tabview: 'audio-video',
      headerText: '오디오 설정',

      showDenied: false,

      //device list
      videoDevices: [],
      micDevices: [],
      speakerDevices: [],
    }
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
          throw 'device access deined'
        }

        if (cameraState.state === 'prompt' || micState.state === 'prompt') {
          await navigator.mediaDevices.getUserMedia({
            audio: true,
            video: true,
          })
        }
        return true
      } catch (err) {
        console.error(err)
      }
    },
    async getMediaDevice() {
      try {
        if (
          !navigator.mediaDevices ||
          !navigator.mediaDevices.enumerateDevices
        ) {
          throw 'enumerateDevices() is not supported'
        }

        const devices = await navigator.mediaDevices.enumerateDevices()
        devices.forEach(device => {
          if (device.kind === 'videoinput') {
            this.videoDevices.push(device)
          } else if (device.kind === 'audioinput') {
            this.micDevices.push(device)
          } else if (device.kind === 'audiooutput') {
            this.speakerDevices.push(device)
          }
        })
      } catch (err) {
        console.error(err)
      }
    },
  },

  /* Lifecycles */
  async created() {
    try {
      const permission = await this.getPermission()

      if (permission) {
        await this.getMediaDevice()
      } else {
        this.showDenied = true
      }
    } catch (err) {
      console.error(err)
    }
  },
  mounted() {},
}
</script>
