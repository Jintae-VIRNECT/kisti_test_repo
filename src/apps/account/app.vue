<template>
  <account-layout></account-layout>
</template>

<script>
import AccountLayout from 'components/account/AccountLayout'

export default {
  name: 'container',
  components: {
    AccountLayout,
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
