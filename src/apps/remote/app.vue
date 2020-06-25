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

    this.mx_initLang()
  },
}
</script>
