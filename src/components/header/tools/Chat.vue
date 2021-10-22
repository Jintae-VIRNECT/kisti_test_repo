<template>
  <tooltip :content="`${$t('common.chatting')} on/off`">
    <toggle-button
      slot="body"
      :customClass="
        `toggle-header__small header-tools__chatbox ${chatBox ? 'visible' : ''}`
      "
      :description="`${$t('common.chatting')} on/off`"
      size="2.429rem"
      :activeSrc="require('assets/image/call/ic_chat_toggle.svg')"
      :inactiveSrc="require('assets/image/call/ic_chat_toggle.svg')"
      @action="toggle"
      :toggle="chatBox"
      :active="active"
    ></toggle-button>
  </tooltip>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'

import Tooltip from 'Tooltip'
import ToggleButton from 'ToggleButton'
export default {
  name: 'Chat',
  components: {
    Tooltip,
    ToggleButton,
  },
  data() {
    return {
      active: false,
    }
  },
  computed: {
    ...mapGetters(['chatBox', 'chatList']),
  },
  watch: {
    'chatList.length': 'listChange',
  },
  methods: {
    ...mapActions(['toggleChat']),
    toggle() {
      this.active = false
      this.toggleChat(!this.chatBox)
    },
    listChange() {
      if (this.chatBox) return

      if (this.chatList.length > 1) {
        this.active = true
      }
    },
  },
}
</script>

<style lang="scss"></style>
