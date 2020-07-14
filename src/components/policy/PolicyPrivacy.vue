<template>
  <main
    class="wrap_terms wrap_policy wrap_policy2"
    v-html="privacyPolicy"
  ></main>
</template>

<script>
import privacyPolicy from 'assets/html/policy/privacyPolicy.html'

export default {
  name: 'policy-privacy',
  data() {
    return {
      privacyPolicy,
    }
  },
}
</script>
<style lang="scss"></style>
