<template>
  <div class="main-body stream">
    <stream-tools
      v-if="!isMobileSize || (isMobileSize && viewForce)"
    ></stream-tools>
    <menus v-if="!isMobileSize"></menus>
    <main-video></main-video>
    <mobile-stt-button v-if="isSttBtnVisible"></mobile-stt-button>
    <div class="mobile-stt-layer" :class="{ active: isSttActivated }">
      <transition name="hide-bottom">
        <chat-speech
          v-if="isSttActivated"
          @hidespeech="useStt(false)"
        ></chat-speech>
      </transition>
    </div>
  </div>
</template>

<script>
import StreamTools from './tools/StreamTools'
import Menus from './tools/Menus'
import MainVideo from './stream/MainVideo'
import MobileSttButton from './stream/partials/MobileSttButton'
import ChatSpeech from './subview/partials/ChatSpeech'
import { getIOSversion } from 'utils/appCheck'
import { mapActions, mapGetters } from 'vuex'

export default {
  name: 'ServiceStream',
  components: {
    StreamTools,
    Menus,
    MainVideo,
    MobileSttButton,
    ChatSpeech,
  },
  computed: {
    ...mapGetters(['viewForce', 'translate', 'usingStt']),
    isSttBtnVisible() {
      const version = getIOSversion()

      if (version > 0 && version < 14) {
        return false
      } else {
        return this.translate.flag && this.isMobileSize
      }
    },
    isSttActivated() {
      return this.isMobileSize && this.usingStt && this.translate.sttSync
    },
  },
  methods: {
    ...mapActions(['useStt']),
  },
}
</script>
