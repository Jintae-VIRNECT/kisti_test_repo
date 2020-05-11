<template>
  <div>
    <header class="no-sidebar">
      <the-header :showSection="showSection" />
    </header>
    <div>
      <main>
        <nuxt />
      </main>
    </div>
  </div>
</template>

<script>
import TheHeader from 'WC-Modules/vue/components/header/TheHeader'

export default {
  middleware: 'default',
  components: {
    TheHeader,
  },
  data() {
    return {
      showSection: {
        profile: true,
        link: true,
      },
    }
  },
}
</script>
