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
          <device-denied
            :visible.sync="showDenied"
            :modalLess="true"
          ></device-denied>

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
            <set-resolution></set-resolution>
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
import SetResolution from '../section/WorkspaceSetResolution'
//import SetVideo from '../section/WorkspaceSetVideo'
import DeviceDenied from 'components/workspace/modal/WorkspaceDeviceDenied'
import { getPermission } from 'utils/deviceCheck'
export default {
  name: 'WorkspaceSetting',
  components: {
    SetAudio,
    SetLanguage,
    SetRecord,
    SetResolution,
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
      const permission = await getPermission()

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
