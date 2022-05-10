<template>
  <div>
    <Header
      :env="$env"
      :subTitle="$t('home.title')"
      :showStatus="showSection"
      :userInfo="auth.myInfo"
      :urls="$url"
      @logout="$store.commit('auth/LOGOUT')"
    />
    <VirnectErrorPage
      :type="error.type"
      :code="error.statusCode"
      :message="error.message"
    />
  </div>
</template>

<script>
export default {
  layout: 'empty',
  props: ['error'],
  data() {
    return {
      showSection: {
        profile: true,
        link: true,
      },
    }
  },
  computed: {
    auth() {
      return this.$store.getters['auth/auth']
    },
  },
  beforeMount() {
    this.$store.dispatch('auth/getAuth')
  },
}
</script>

<style lang="scss" scoped>
.virnect-error-page {
  padding-top: 35px;
}
</style>
