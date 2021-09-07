<template>
  <div class="guest-web">
    <header-section></header-section>
    <vue2-scrollbar
      classes="remote-wrapper"
      ref="wrapperScroller"
      :onMaxScroll="handleMaxScroll"
    >
      <div class="workspace-wrapper">
        <guest-welcome></guest-welcome>
        <guest-tab ref="tabSection"></guest-tab>
      </div>
    </vue2-scrollbar>
  </div>
</template>

<script>
import HeaderSection from 'components/header/Header'
import GuestWelcome from './section/GuestWelcome'
import GuestTab from './section/GuestTab'

export default {
  name: 'GuestWeb',
  components: {
    HeaderSection,
    GuestWelcome,
    GuestTab,
  },
  methods: {
    handleMaxScroll(event) {
      this.$eventBus.$emit('scroll:end', event)
    },
  },
}
</script>

<style></style>
