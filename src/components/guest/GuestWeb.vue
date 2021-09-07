<template>
  <div class="guest-web">
    <div class="workspace-wrapper">
      <guest-welcome></guest-welcome>
      <guest-tab ref="tabSection"></guest-tab>
    </div>
  </div>
</template>

<script>
import GuestWelcome from './section/GuestWelcome'
import GuestTab from './section/GuestTab'

export default {
  name: 'GuestWeb',
  components: {
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
