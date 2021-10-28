<template>
  <div class="guest-web">
    <div class="workspace-wrapper">
      <guest-welcome
        ref="guest-welcome"
        @joinText="onJoinTextChanged"
      ></guest-welcome>
      <guest-tab
        ref="tabSection"
        :joinText="joinText"
        @joinAsGuest="joinAsGuest"
      ></guest-tab>
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
  data() {
    return {
      joinText: '',
    }
  },
  methods: {
    joinAsGuest() {
      this.$nextTick(() => {
        this.$refs['guest-welcome'].joinAsGuest()
      })
    },
    onJoinTextChanged(joinText) {
      this.joinText = joinText
    },
  },
}
</script>

<style></style>
