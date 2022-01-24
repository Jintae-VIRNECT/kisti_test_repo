<template>
  <header class="header service">
    <div v-if="!isMobileSize" class="header-service">
      <img
        v-if="logo !== false"
        @error="logoError"
        class="header-logo"
        :src="logo"
      />
      <img v-else class="header-logo" src="~assets/image/logo_symtext.svg" />
      <header-lnb></header-lnb>

      <header-tools :callTime="callTime"></header-tools>
    </div>
    <service-mobile-header v-else :callTime="callTime"></service-mobile-header>
  </header>
</template>

<script>
import { WHITE_LOGO, DEFAULT_LOGO } from 'configs/env.config'
import HeaderLnb from './partials/HeaderServiceLnb'
import HeaderTools from './partials/HeaderServiceTools'
import ServiceMobileHeader from './ServiceMobileHeader'

export default {
  name: 'ServiceHeader',
  components: {
    HeaderLnb,
    HeaderTools,
    ServiceMobileHeader,
  },
  data() {
    return {
      logo: WHITE_LOGO,

      runnerID: null,
      callStartTime: null,
      callTime: null,
    }
  },
  methods: {
    logoError() {
      this.logo = DEFAULT_LOGO
    },

    timeRunner() {
      clearInterval(this.runnerID)
      this.runnerID = setInterval(() => {
        const diff = this.$dayjs().unix() - this.callStartTime

        this.callTime = this.$dayjs.duration(diff, 'seconds').as('milliseconds')
      }, 1000)
    },
  },
  /* Lifecycles */
  mounted() {
    this.callStartTime = this.$dayjs().unix()
    this.timeRunner()
  },
  beforeDestroy() {
    this.callStartTime = null
    clearInterval(this.runnerID)
  },
}
</script>
