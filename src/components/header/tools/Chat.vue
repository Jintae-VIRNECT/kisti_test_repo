<template>
  <tooltip :content="`${$t('common.chatting')} on/off`">
    <toggle-button
      slot="body"
      customClass="toggle-header__small header-tools__chatbox"
      :description="`${$t('common.chatting')} on/off`"
      size="2.429rem"
      :activeSrc="require('assets/image/call/ic_chat_toggle.svg')"
      @action="toggle"
      :toggle="chatBox"
      :active="chatBox"
    ></toggle-button>
  </tooltip>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'

import Tooltip from 'Tooltip'
import ToggleButton from 'ToggleButton'
export default {
  name: 'Mic',
  components: {
    Tooltip,
    ToggleButton,
  },
  computed: {
    ...mapGetters(['chatBox']),
  },
  methods: {
    ...mapActions(['toggleChat']),
    toggle() {
      this.toggleChat(!this.chatBox)
    },
  },
}
</script>

<style lang="scss"></style>
