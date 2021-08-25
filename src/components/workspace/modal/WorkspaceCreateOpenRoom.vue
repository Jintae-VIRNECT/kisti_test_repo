<template>
  <modal
    :title="$t('workspace.create_open')"
    width="28.786em"
    :showClose="true"
    :visible.sync="visibleFlag"
    :beforeClose="beforeClose"
    customClass="openroom-modal"
  >
    <div class="openroom">
      <open-room-info
        :roomInfo="roomInfo"
        :btnLoading="clicked"
        @startRemote="startRemote"
      ></open-room-info>
    </div>
  </modal>
</template>

<script>
import Modal from 'Modal'
import { mapActions } from 'vuex'
import OpenRoomInfo from '../partials/ModalCreateOpenRoomInfo'

import { getHistorySingleItem } from 'api/http/history'

import confirmMixin from 'mixins/confirm'
import roomMixin from 'mixins/room'

export default {
  name: 'WorkspaceCreateOpenRoom',
  mixins: [confirmMixin, roomMixin],
  components: {
    Modal,
    OpenRoomInfo,
  },
  data() {
    return {
      visibleFlag: false,
      roomInfo: {},
      clicked: false,
    }
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    sessionId: {
      type: String,
      default: '',
    },
  },
  watch: {
    visible(flag) {
      this.visibleFlag = flag
      if (flag === true && this.sessionId.length > 0) {
        this.getInfo()
      }
    },
    isMobile: {
      immediate: true,
      handler: function(newVal) {
        if (newVal) this.beforeClose()
      },
    },
  },
  methods: {
    ...mapActions(['setRoomInfo', 'roomClear', 'updateAccount']),
    async getInfo() {
      try {
        this.roomInfo = await getHistorySingleItem({
          workspaceId: this.workspace.uuid,
          sessionId: this.sessionId,
        })
        for (let member of this.roomInfo.memberList) {
          if (member.uuid !== this.account.uuid) {
            this.selection.push(member)
          }
        }
      } catch (err) {
        console.error(err)
      }
    },
    reset() {
      this.selection = []
    },
    beforeClose() {
      this.$emit('update:visible', false)
    },
  },

  /* Lifecycles */
  created() {},
  mounted() {},
}
</script>

<style lang="scss" src="assets/style/workspace/workspace-openroom.scss"></style>
