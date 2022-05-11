<template>
  <ul class="hover-box">
    <li
      v-for="(lang, key) of localeArr"
      :key="key"
      :class="{ active: locale === key }"
    >
      <button @click="changeLang(key)">{{ lang }}</button>
    </li>
    <li
      v-if="!/(production|onpremise)/.test(env)"
      :class="{ active: locale == 'key' }"
    >
      <button @click="changeLang('key')">Key</button>
    </li>
  </ul>
</template>
<script>
import Cookies from 'js-cookie'
export default {
  props: {
    localeArr: Object,
    env: String,
    showStatus: Object,
    isMobile: Boolean,
  },
  computed: {
    locale() {
      return this.$i18n.locale
    },
  },
  methods: {
    changeLang(locale) {
      const cookieOption = {
        domain:
          location.hostname.split('.').length === 3
            ? location.hostname.replace(/.*?\./, '')
            : location.hostname,
      }

      Cookies.set('lang', locale, cookieOption)
      this.$i18n.locale = locale
      document.documentElement.lang = locale
    },
  },
}
</script>
