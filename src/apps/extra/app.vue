<template>
  <router-view></router-view>
</template>

<script>
export default {
  name: 'container',
  methods: {
    windowResizeHandler(e) {
      const html = document.querySelector('html')
      const windowWidth = window.innerWidth
      let type = 'desktop'

      if (windowWidth <= 1024) {
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
    let type = 'desktop'

    if (windowWidth <= 1024) {
      type = 'tablet'
    } else if (windowWidth < 768) {
      type = 'mobile'
    }
    this.$store.dispatch('setDeviceType', type)
    html.setAttribute('data-screen', type)
    window.addEventListener('resize', this.windowResizeHandler)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.windowResizeHandler)
  },
}
</script>
