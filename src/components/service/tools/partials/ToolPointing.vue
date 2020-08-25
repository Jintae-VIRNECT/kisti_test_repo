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
    ...mapGetters(['allowPointing', 'viewForce']),
    canPointing() {
      if (this.disabled) {
        return false
      }
      if (!this.viewForce) {
        return false
      }
      if (this.account.roleType === ROLE.LEADER) {
        return true
      }
      if (this.allowPointing) {
        return true
      } else {
        return false
      }
    },
  },
  methods: {
    pointing() {
      if (!this.viewForce) {
        if (this.account.roleType === ROLE.LEADER) {
          // TODO: MESSAGE
          this.toastDefault('영상을 고정해야 포인팅이 가능합니다.')
        } else {
          // TODO: MESSAGE
          this.toastDefault('리더가 영상을 고정해야 포인팅이 가능합니다.')
        }
        return
      }
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
