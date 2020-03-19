<template>
  <remote-layout></remote-layout>
</template>

<script>
import RemoteLayout from 'components/RemoteLayout'

export default {
  name: 'container',
  components: {
    RemoteLayout,
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
    html.setAttribute('data-screen', type)
  },
}
</script>
