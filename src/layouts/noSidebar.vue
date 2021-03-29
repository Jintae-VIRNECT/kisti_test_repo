<template>
  <div class="no-sidebar">
    <VirnectHeader
      :showStatus="showSection"
      :userInfo="auth.myInfo"
      :urls="$url"
      @logout="$store.commit('auth/LOGOUT')"
    />
    <div>
      <main>
        <nuxt />
      </main>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  middleware: 'default',
  head() {
    return {
      title: `VIRNECT | ${this.$t('common.workstation')}`,
    }
  },
  data() {
    return {
      showSection: {
        profile: true,
        link: true,
      },
    }
  },
  computed: {
    ...mapGetters({
      auth: 'auth/auth',
    }),
  },
  mounted() {
    // 콘솔 표시
    console.log(
      `%cVirnect Workstation v${this.$config.VERSION}`,
      'font-size: 20px; color: #1468e2',
    )
    console.log(`env: ${this.$config.VIRNECT_ENV}`)
    console.log(`timeout: ${this.$config.API_TIMEOUT}`)

    this.$store.dispatch('auth/getAuth')
  },
}
</script>

<style lang="scss">
.no-sidebar main {
  min-width: 0;
}
</style>
