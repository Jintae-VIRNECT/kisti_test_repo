<template>
  <ul class="login-wrap">
    <li class="login-btn">
      <a :href="`${urls.console}/?continue=${currentUrl}`" v-if="!isMobile"
        >Log in</a
      >
      <VirnectButton label="Log in" block @onClick="onClick('login')" v-else />
    </li>
    <li class="singup-btn">
      <a :href="`${urls.console}/terms`" v-if="!isMobile">Sign up</a>
      <VirnectButton
        label="Sign up"
        type="gray"
        block
        @onClick="onClick('signup')"
        v-else
      />
    </li>
  </ul>
</template>

<script>
export default {
  props: {
    urls: Object,
    isMobile: Boolean,
  },
  data() {
    return {
      currentUrl: '',
    }
  },
  methods: {
    onClick(str) {
      if (str === 'signup') {
        location.href = `${this.urls.console}/terms`
        // window.open(`${this.urls.console}/terms`)
      } else {
        location.href = `${this.urls.console}/?continue=${this.currentUrl}`
        // window.open(`${this.urls.console}/?continue=${this.currentUrl}`)
      }
    },
  },
  mounted() {
    this.currentUrl = location.href
  },
}
</script>
