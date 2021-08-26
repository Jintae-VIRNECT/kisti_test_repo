<template>
  <main class="wrap_terms wrap_policy wrap_policy2" v-html="CookieAgree"></main>
</template>

<script>
import CookieAgree from 'assets/html/policy/cookieAgree.html'

export default {
  name: 'cookie-agree',
  data() {
    return {
      CookieAgree,
    }
  },
}
</script>
<style lang="scss"></style>
