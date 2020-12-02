<template>
  <!-- <div class="service-wrapper">Hello World</div> -->
  <router-view :key="key"></router-view>
</template>

<script>
export default {
  name: 'RemoteLayout',
  data() {
    return {
      key: 1,
      attr: '',
    }
  },
  methods: {
    viewPointSetting(e) {
      setTimeout(() => {
        let viewport = document.querySelector('meta[name=viewport]')
        this.attr = viewport.getAttribute('content')
        document.documentElement.style.setProperty('overflow', 'auto')
        const metaViewport = document.querySelector('meta[name=viewport]')
        // console.log(e.currentTarget.innerHeight)
        const initialHeight =
          e && e.currentTarget
            ? e.currentTarget.innerHeight
            : window.innerHeight
        console.log(initialHeight)
        metaViewport.setAttribute(
          'content',
          `height=${initialHeight}px,${this.attr}`,
        )
      }, 1000)
    },
  },
  created() {
    this.$eventBus.$on('reJoin', () => {
      this.key++
    })
    if (this.isTablet) {
      document.body.onorientationchange = this.viewPointSetting
      // window.addEventListener('orientationchange', this.viewPointSetting)
      this.viewPointSetting()
    }
  },
  beforeDestroy() {
    this.$eventBus.$off('reJoin', () => {
      this.key++
    })
  },
}
</script>

<style lang="scss" src="assets/style/layout.scss"></style>
