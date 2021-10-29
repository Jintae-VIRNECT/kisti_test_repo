<template>
  <tooltip :content="`${$t('common.chatting')} on/off`">
    <toggle-button
      slot="body"
      :customClass="
        `toggle-header__small header-tools__chatbox ${chatBox ? 'visible' : ''}`
      "
      :description="`${$t('common.chatting')} on/off`"
      :size="iconSize"
      :activeSrc="chatIcon"
      :inactiveSrc="chatIcon"
      @action="toggle"
      :toggle="chatBox"
      :active="chatActive"
    ></toggle-button>
  </tooltip>
</template>

<script>
import { mapGetters, mapActions, mapMutations } from 'vuex'

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
    ...mapGetters(['chatBox', 'chatList', 'chatActive']),
    iconSize() {
      if (this.isMobileSize) return '3.6rem'
      else return '2.429rem'
    },
    chatIcon() {
      if (this.isMobileSize)
        return require('assets/image/call/ic_chat_toggle_new.svg')
      else return require('assets/image/call/ic_chat_toggle.svg')
    },
  },
  watch: {
    'chatList.length': 'listChange',
  },
  methods: {
    ...mapMutations(['SET_CHAT_ACTIVE']),
    ...mapActions(['toggleChat']),
    toggle() {
      //this.active = false
      this.SET_CHAT_ACTIVE(false)
      this.toggleChat(!this.chatBox)
    },
    listChange() {
      if (this.chatBox) return

      if (this.chatList.length > 1) {
        this.active = true
        this.SET_CHAT_ACTIVE(true)
      }
    },
  },
}
</script>

<style lang="scss"></style>
