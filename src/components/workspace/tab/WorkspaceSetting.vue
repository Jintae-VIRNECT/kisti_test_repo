<template>
  <div class="tab-view">
    <div class="setting-wrapper offsetwidth">
      <div class="setting-nav">
        <div class="setting-nav__header">{{ $t('workspace.setting') }}</div>
        <div
          class="setting-nav__menu"
          v-for="(menu, idx) of menus"
          :key="menu.key"
          :class="{ active: tabIdx === idx }"
          @click="tabChange(idx)"
        >
          {{ menu.text }}
        </div>
      </div>

      <div class="setting-view">
        <div class="setting-view__header">{{ menus[tabIdx].text }}</div>

        <div class="setting-view__body">
          <template v-if="menus[tabIdx].key === 'video'">
            <set-video
              :videoDevices="videoDevices"
              ref="videoDevices"
            ></set-video>
          </template>

          <template v-else-if="menus[tabIdx].key === 'audio'">
            <set-audio
              :micDevices="micDevices"
              :speakerDevices="speakerDevices"
            ></set-audio>

            <mic-test> </mic-test>
          </template>

          <template v-else-if="menus[tabIdx].key === 'record'">
            <set-record v-if="!isTablet"></set-record>
            <set-server-record v-if="onpremise"></set-server-record>
          </template>
          <template v-else-if="menus[tabIdx].key === 'language'">
            <set-language></set-language>
          </template>
          <template v-else-if="menus[tabIdx].key === 'translate'">
            <set-translate></set-translate>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
// import SttTest from '../section/WorkspaceSttTest'
import SetVideo from '../section/WorkspaceSetVideo'
import SetAudio from '../section/WorkspaceSetAudio'
import SetLanguage from '../section/WorkspaceSetLanguage'
import SetTranslate from '../section/WorkspaceSetTranslate'
import SetRecord from '../section/WorkspaceSetRecord'
import MicTest from '../section/WorkspaceMicTest'
import SetServerRecord from '../section/WorkspaceSetServerRecord'
import { getPermission, getUserMedia } from 'utils/deviceCheck'
import { RUNTIME, RUNTIME_ENV } from 'configs/env.config'
import { mapGetters } from 'vuex'
export default {
  name: 'WorkspaceSetting',
  components: {
    // SttTest,
    SetVideo,
    SetAudio,
    SetLanguage,
    SetTranslate,
    SetRecord,
    SetServerRecord,
    MicTest,
  },
  data() {
    return {
      tabIdx: 0,

      //device list
      videoDevices: [],
      micDevices: [],
      speakerDevices: [],
    }
  },
  computed: {
    ...mapGetters(['useTranslate']),
    menus() {
      let menu
      if (this.isSafari && !this.onpremise) {
        menu = [
          {
            key: 'video',
            text: this.$t('workspace.setting_video'),
          },
          {
            key: 'audio',
            text: this.$t('workspace.setting_audio'),
          },
          {
            key: 'language',
            text: this.$t('workspace.setting_language'),
          },
        ]
      } else {
        menu = [
          {
            key: 'video',
            text: this.$t('workspace.setting_video'),
          },
          {
            key: 'audio',
            text: this.$t('workspace.setting_audio'),
          },
          {
            key: 'record',
            text: this.$t('workspace.setting_record'),
          },
          {
            key: 'language',
            text: this.$t('workspace.setting_language'),
          },
        ]
      }
      if (this.useTranslate) {
        menu.push({
          key: 'translate',
          text: this.$t('workspace.setting_translate'),
        })
      }
      return menu
    },
    onpremise() {
      return RUNTIME.ONPREMISE === RUNTIME_ENV
    },
  },
  methods: {
    tabChange(idx) {
      this.$eventBus.$emit('popover:close')
      this.$eventBus.$emit('scroll:reset:workspace')
      this.$nextTick(() => {
        this.tabIdx = idx
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
        const videos = [],
          mics = [],
          speakers = []
        devices.forEach(device => {
          if (device.kind === 'videoinput') {
            videos.push(device)
          } else if (device.kind === 'audioinput') {
            mics.push(device)
          } else if (device.kind === 'audiooutput') {
            speakers.push(device)
          }
        })
        return { videos, mics, speakers }
      } catch (err) {
        console.error(err)
      }
    },
    async onDeviceChange() {
      this.logger('device', 'changed')
      let devices = await this.getMediaDevice()
      this.videoDevices = devices.videos
      this.micDevices = devices.mics
      this.speakerDevices = devices.speakers
    },
  },

  /* Lifecycles */
  async created() {
    navigator.mediaDevices.ondevicechange = this.onDeviceChange
    const permission = await getPermission()

    if (permission === true) {
      const devices = await this.getMediaDevice()
      this.videoDevices = devices.videos
      this.micDevices = devices.mics
      this.speakerDevices = devices.speakers
    } else if (permission === 'prompt') {
      let devices = await this.getMediaDevice()
      let video = false,
        audio = false
      if (devices.videos.length > 0) {
        video = true
      }
      if (devices.mics.length > 0) {
        audio = true
      }
      try {
        const stream = await getUserMedia(audio, video)
        stream.getTracks().forEach(track => {
          track.stop()
        })
        devices = await this.getMediaDevice()
      } catch (err) {
        if (err.name && err.name === 'NotAllowedError') {
          this.$eventBus.$emit('devicedenied:show')
          return
        }
      }
      this.videoDevices = devices.videos
      this.micDevices = devices.mics
      this.speakerDevices = devices.speakers
    } else {
      this.$eventBus.$emit('devicedenied:show')
    }
  },
  beforeDestroy() {
    navigator.mediaDevices.ondevicechange = () => {}
  },
}
</script>
