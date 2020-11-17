<template>
  <audio preload="auto" ref="ttsAudio" autoplay>
    <source :src="audioSrc" />
  </audio>
</template>
<script>
import { mapGetters } from 'vuex'
import { tts } from 'plugins/remote/translate'
export default {
  data() {
    return {
      audioSrc: '',
    }
  },
  computed: {
    ...mapGetters(['translate']),
  },
  methods: {
    doTts(message) {
      if (!this.translate.ttsAllow) return
      if (!message || message.length === 0) return
      const startTime = Date.now()
      tts(message, this.translate.code).then(res => {
        const ttsTime = Date.now() - startTime
        this.logger('TTS', 'DURING TIME: ', ttsTime)
        this.audioSrc = 'data:audio/wav;base64,' + res
        this.$refs['ttsAudio'].load()
      })
    },
  },
}
</script>
