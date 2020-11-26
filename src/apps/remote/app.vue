<template>
  <remote-layout></remote-layout>
</template>

<script>
import RemoteLayout from 'components/RemoteLayout'
import langMixin from 'mixins/language'
export default {
  name: 'container',
  mixins: [langMixin],
  components: {
    RemoteLayout,
  },
  methods: {
    windowResizeHandler() {
      const html = document.querySelector('html')
      const windowWidth = window.innerWidth
      const windowHeight = window.innerHeight
      let type = 'desktop'

      if (windowWidth <= 1024 || windowHeight <= 680) {
        type = 'tablet'
      } else if (windowWidth < 768) {
        type = 'mobile'
      }
      this.$store.dispatch('setDeviceType', type)
      html.setAttribute('data-screen', type)
    },
  },

  /* Lifecycles */
  created() {
    const html = document.querySelector('html')
    const windowWidth = window.innerWidth
    const windowHeight = window.innerHeight
    let type = 'desktop'

    if (windowWidth <= 1024 || windowHeight <= 680) {
      type = 'tablet'
    } else if (windowWidth < 768) {
      type = 'mobile'
    }
    this.$store.dispatch('setDeviceType', type)
    html.setAttribute('data-screen', type)
    window.addEventListener('resize', this.windowResizeHandler)
    this.mx_initLang()
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.windowResizeHandler)
  },
}
</script>
