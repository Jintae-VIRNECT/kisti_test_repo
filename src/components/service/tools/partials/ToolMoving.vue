<template>
  <tool-button
    :text="$t('화면 이동')"
    :active="viewAction === STREAM_MOVING"
    :disabled="!canMoving"
    :src="require('assets/image/call/ic_move.svg')"
    @click="moving"
  ></tool-button>
</template>

<script>
import toolMixin from './toolMixin'
import toastMixin from 'mixins/toast'
import { ACTION } from 'configs/view.config'
import { ROLE } from 'configs/remote.config'
import { mapGetters } from 'vuex'
export default {
  name: 'MovingTool',
  mixins: [toolMixin, toastMixin],
  data() {
    return {
      STREAM_MOVING: ACTION.STREAM_MOVING,
    }
  },
  computed: {
    ...mapGetters(['viewForce']),
    canMoving() {
      if (this.disabled) {
        return false
      }
      if (!this.viewForce) {
        return false
      }
      if (this.account.roleType === ROLE.LEADER) {
        return true
      }
      return false
    },
  },
  methods: {
    moving() {
      if (!this.viewForce) {
        this.toastDefault(this.$t('service.toast_no_sharing'))
        return
      }
      if (this.viewAction !== ACTION.STREAM_MOVING) {
        this.setAction('moving')
      }
    },
  },
}
</script>
