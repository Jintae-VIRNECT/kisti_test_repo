<template>
  <section class="tab-view">
    <div class="mobile-setting-wrapper">
      <nav class="setting-nav">
        <perfect-scrollbar>
          <ul>
            <tab-button
              v-for="(menu, idx) of menus"
              :key="menu.key"
              :text="menu.text"
              :active="tabIdx === idx"
              @click.native="tabChange(idx)"
            ></tab-button>
          </ul>
        </perfect-scrollbar>
      </nav>

      <div class="setting-view" :class="menus[tabIdx].key">
        <div class="setting-view__header">{{ menus[tabIdx].mobileText }}</div>

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
</template>

<script>
import { PerfectScrollbar } from 'vue2-perfect-scrollbar'
import TabButton from '../../partials/WorkspaceTabButton'
import SetVideo from './WorkspaceSetVideo'
import SetAudio from './WorkspaceSetAudio'
import SetLanguage from './WorkspaceSetLanguage'
import MicTest from './WorkspaceMicTest'
import { mapGetters } from 'vuex'

export default {
  components: {
    PerfectScrollbar,
    TabButton,
    SetVideo,
    SetAudio,
    MicTest,
    SetLanguage,
    SetTranslate: () => import('./WorkspaceSetTranslate'),
    SetRecord: () => import('./WorkspaceSetRecord'),
    SetServerRecord: () => import('./WorkspaceSetServerRecord'),
    SetFeature: () => import('./WorkspaceSetFeature'),
  },
  props: {
    menus: {
      type: Array,
    },
    tabIdx: {
      type: Number,
    },
    videoDevices: {
      type: Array,
      default: () => [],
    },
    micDevices: {
      type: Array,
      default: () => [],
    },
    speakerDevices: {
      type: Array,
      default: () => [],
    },
  },
  computed: {
    ...mapGetters(['useRecording', 'useLocalRecording']),
  },
  methods: {
    tabChange(idx) {
      this.$emit('tabChange', idx)
      this.$nextTick(() => {
        this.$eventBus.$emit('settingTabChanged')
      })
    },
  },
}
</script>

<style src="vue2-perfect-scrollbar/dist/vue2-perfect-scrollbar.css" />
