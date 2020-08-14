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
          <device-denied
            :visible.sync="showDenied"
            :modalLess="true"
          ></device-denied>

          <template v-if="menus[tabIdx].key === 'audio-video'">
            <set-audio
              :micDevices="micDevices"
              :speakerDevices="speakerDevices"
              :videoDevices="videoDevices"
            ></set-audio>

            <mic-test> </mic-test>
          </template>

          <template v-else-if="menus[tabIdx].key === 'video-record'">
            <set-record></set-record>
            <set-resolution></set-resolution>
          </template>
          <template v-else-if="menus[tabIdx].key === 'language'">
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
    DeviceDenied,
  },
  data() {
    return {
      tabIdx: 0,

      showDenied: false,

      //device list
      videoDevices: [],
      micDevices: [],
      speakerDevices: [],
    }
  },
  computed: {
    menus() {
      return [
        {
          key: 'audio-video',
          // text: this.$t('workspace.setting_audio'),
          text: '비디오 및 오디오 설정',
        },
        {
          key: 'video-record',
          text: this.$t('workspace.setting_record'),
        },
        {
          key: 'language',
          text: this.$t('workspace.setting_language'),
        },
      ]
    },
  },
  methods: {
    tabChange(idx) {
      this.$eventBus.$emit('popover:close')
      this.$eventBus.$emit('scroll:reset:workspace')
      if (this.menus[idx].key === 'language' && this.checkBeta()) return
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
      console.log(permission)

      if (permission === true) {
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
