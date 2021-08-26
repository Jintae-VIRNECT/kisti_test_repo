<template>
  <main
    class="wrap_terms wrap_policy wrap_policy2"
    v-html="serviceTerms"
  ></main>
</template>

<script>
import serviceTerms from 'assets/html/policy/serviceTerms.html'

export default {
  name: 'policy-terms',
  data() {
    return {
      serviceTerms,
    }
  },
}
</script>
