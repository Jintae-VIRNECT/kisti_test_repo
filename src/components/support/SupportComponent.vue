<template>
  <error-component>
    <button
      slot="error-body-button"
      v-if="isScreenDesktop && !isOnpremise"
      @click="pcWeb"
    >
      {{ $t('support.pc_web_button') }}
    </button>
  </error-component>
</template>

<script>
import ErrorComponent from '../error/ErrorComponent'
import { getConfigs } from 'utils/auth'

export default {
  name: 'SupportComponent',
  async beforeRouteEnter(to, from, next) {
    await getConfigs()
    next()
  },
  components: {
    ErrorComponent,
  },
  methods: {
    pcWeb() {
      window.open('https://www.google.com/intl/ko/chrome/')
    },
    mobile() {
      window.open(
        'https://play.google.com/store/apps/details?id=com.virnect.remote.mobile&hl=ko',
      )
    },
    smartGlasses() {
      //미정
    },
  },
}
</script>
