<template>
  <button class="mobile-stt-btn" @click="doStt"></button>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import toastMixin from 'mixins/toast'

export default {
  mixins: [toastMixin],
  computed: {
    ...mapGetters(['mic']),
  },
  methods: {
    ...mapActions(['useStt']),
    doStt() {
      if (!this.mic.isOn) {
        this.toastDefault(this.$t('service.stt_mic_off'))
        return
      }
      this.useStt(true)
    },
  },
}
</script>

<style lang="scss">
.mobile-stt-btn {
  position: fixed;
  top: 68.5%;
  right: 1.6rem;
  width: 4rem;
  height: 4rem;
  border-radius: 6px;
  background-color: #121314;
  background-image: url('~assets/image/call/mdpi_icon_stt.svg');
}
</style>
