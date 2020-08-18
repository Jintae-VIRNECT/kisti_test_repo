<template>
  <tool-button
    :text="$t('service.tool_pointing')"
    :active="viewAction === STREAM_POINTING"
    :disabled="!canPointing"
    :src="require('assets/image/ic_pointing.svg')"
    @click="pointing"
  ></tool-button>
</template>

<script>
import toolMixin from './toolMixin'
import toastMixin from 'mixins/toast'
import { ACTION } from 'configs/view.config'
import { ROLE } from 'configs/remote.config'
import { mapGetters } from 'vuex'
export default {
  name: 'PointingTool',
  mixins: [toolMixin, toastMixin],
  data() {
    return {
      STREAM_POINTING: ACTION.STREAM_POINTING,
    }
  },
  computed: {
    ...mapGetters(['control']),
    canPointing() {
      if (this.disabled) {
        return false
      }
      if (this.account.roleType === ROLE.EXPERT_LEADER) {
        return true
      }
      if (this.control.pointing) {
        return true
      } else {
        return false
      }
    },
  },
  methods: {
    pointing() {
      if (!this.canPointing) {
        // TODO: MESSAGE
        this.toastDefault(this.$t('service.tool_pointing_block'))
        return
      }
      if (this.viewAction !== ACTION.STREAM_POINTING) {
        this.setAction('pointing')
      } else {
        this.setAction('default')
      }
    },
  },
}
</script>
