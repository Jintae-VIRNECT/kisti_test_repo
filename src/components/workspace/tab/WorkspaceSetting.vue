<template>
  <div style="display:flex; flex:1;">
    <section v-if="!isMobileSize" class="tab-view">
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
          <div class="setting-view__header">
            {{ menus[tabIdx].text }}
          </div>

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
              <set-record v-if="!isTablet && useLocalRecording"></set-record>
              <set-server-record v-if="useRecording"></set-server-record>
            </template>
            <template v-else-if="menus[tabIdx].key === 'language'">
              <set-language></set-language>
            </template>
            <template v-else-if="menus[tabIdx].key === 'translate'">
              <set-translate></set-translate>
            </template>
            <template v-else-if="menus[tabIdx].key === 'feature'">
              <set-feature></set-feature>
            </template>
          </div>
        </div>
      </div>
    </section>
    <workspace-mobile-setting
      v-else
      :menus="menus"
      :tabIdx="tabIdx"
      :videoDevices="videoDevices"
      :micDevices="micDevices"
      :speakerDevices="speakerDevices"
      @tabChange="tabChange"
    ></workspace-mobile-setting>
  </div>
</template>
<script>
import SetVideo from './setting/WorkspaceSetVideo'
import SetAudio from './setting/WorkspaceSetAudio'
import SetLanguage from './setting/WorkspaceSetLanguage'
import MicTest from './setting/WorkspaceMicTest'
import { getPermission, getUserMedia } from 'utils/deviceCheck'
import { mapGetters } from 'vuex'
export default {
  name: 'WorkspaceSetting',
  components: {
    SetVideo,
    SetAudio,
    MicTest,
    SetLanguage,
    SetTranslate: () => import('./setting/WorkspaceSetTranslate'),
    SetRecord: () => import('./setting/WorkspaceSetRecord'),
    SetServerRecord: () => import('./setting/WorkspaceSetServerRecord'),
    SetFeature: () => import('./setting/WorkspaceSetFeature'),
    WorkspaceMobileSetting: () => import('./setting/WorkspaceMobileSetting'),
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
    ...mapGetters([
      'useTranslate',
      'useRecording',
      'useLocalRecording',
      'restrictedMode',
    ]),
    menus() {
      let menu = [
        {
          key: 'video',
          text: this.$t('workspace.setting_video'),
          mobileText: this.$t('workspace.setting_video_device_choice'),
        },
      ]
      if (!this.isMobileSize) {
        menu.push({
          key: 'audio',
          text: this.$t('workspace.setting_audio'),
        })
      }
      if (!this.isTablet && (this.useLocalRecording || this.useRecording)) {
        menu.push({
          key: 'record',
          text: this.$t('workspace.setting_record'),
          mobileText: this.$t('workspace.setting_server_record'),
        })
      }

      menu.push({
        key: 'language',
        text: this.$t('workspace.setting_language'),
        mobileText: this.$t('workspace.setting_language_choice'),
      })
      if (this.useTranslate) {
        menu.push({
          key: 'translate',
          text: this.$t('workspace.setting_translate'),
          mobileText: this.$t('workspace.setting_translate'),
        })
      }
      if (this.restrictedMode.audio || this.restrictedMode.video) {
        menu.push({
          key: 'feature',
          text: this.$t('workspace.setting_function'),
          mobileText: this.$t('workspace.setting_camera_control'),
        })
      }
      return menu
    },
  },
  watch: {
    menus: {
      handler(curMenus, prevMenus) {
        //PC <-> 모바일 화면 전환간 menu변경시
        //이전에 참조하고 있던 메뉴 유지
        const prevMenu = prevMenus[this.tabIdx].key
        const newMenuIdx = curMenus.findIndex(menu => menu.key === prevMenu)
        this.tabIdx = newMenuIdx >= 0 ? newMenuIdx : 0
      },
      deep: true,
    },
  },
  methods: {
    tabChange(idx) {
      this.$eventBus.$emit('popover:close')
      this.$eventBus.$emit('scroll:reset:workspace')
      this.$eventBus.$emit('search:clear')
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
    this.$eventBus.$emit('search:clear')

    navigator.mediaDevices.ondevicechange = this.onDeviceChange
    const permission = await getPermission({
      video: true,
      audio: true,
    })

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
        const stream = await getUserMedia({ audio, video })
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
