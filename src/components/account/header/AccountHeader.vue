<template>
  <header class="header">
    <div class="header--inner">
      <h1 class="header--logo">
        <a link="/">
          <img src="~assets/image/logo_symtext.svg" alt="remote logo" />
        </a>
      </h1>
    </div>
  </header>
</template>

<script>
export default {
  name: 'AccountHeader',
  data() {
    const lang = this.$i18n.locale
    return {
      lang,
      showOutlink: false,
    }
  },
  watch: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
  beforeDestroy() {},
}
</script>
