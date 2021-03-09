<template>
  <audio ref="ttsAudio" playsinline preload="auto">
    <source :src="audioSrc" />
  </audio>
</template>
<script>
import { mapGetters } from 'vuex'
import { tts } from 'plugins/remote/translate'
export default {
  data() {
    return {
      audioSrc: require('assets/media/end.mp3'),
    }
  },
  computed: {
    ...mapGetters(['translate', 'speaker']),
  },
  methods: {
    async doTts(message) {
      if (!this.speaker.isOn) return
      if (!this.translate.flag) return
      if (!this.translate.ttsAllow) return
      if (!message || message.length === 0) return
      let ttsText = message
      if (message.length > 200) {
        ttsText = message.substr(0, 200)
      }
      await this.sendTts(ttsText)
      this.doTts(message.substr(200))
    },
    sendTts(text) {
      return new Promise(async resolve => {
        const startTime = Date.now()
        const res = await tts(text, this.translate.code)
        const ttsTime = Date.now() - startTime
        this.logger('TTS', 'DURING TIME: ', ttsTime)
        this.audioSrc = 'data:audio/wav;base64,' + res
        const audio = this.$refs['ttsAudio']
        audio.onended = () => {
          resolve(true)
        }
        audio.oncanplay = () => {
          audio.play()
        }
        audio.load()
      })
    },
    loadTtsAudio() {
      this.$refs['ttsAudio'].play()
      this.$refs['ttsAudio'].pause()
      this.$refs['ttsAudio'].muted = false
      window.removeEventListener('touchstart', this.loadTtsAudio)
      this.$refs['ttsAudio'].onloadeddata = () => {}
    },
  },
  mounted() {
    const audio = this.$refs['ttsAudio']
    audio.muted = true
    audio.onloadeddata = () => {
      window.addEventListener('touchstart', this.loadTtsAudio)
    }
    audio.load()
  },
  beforeDestroy() {
    this.$refs['ttsAudio'].onloadeddata = () => {}
    window.removeEventListener('touchstart', this.loadTtsAudio)
  },
}
</script>
