<template>
  <modal
    :title="$t('workspace.create_open')"
    width="28.786em"
    height="46.929em"
    :showClose="true"
    :visible.sync="visibleFlag"
    :beforeClose="beforeClose"
    customClass="openroom-modal"
  >
    <div class="openroom">
      <open-room-info
        :roomInfo="roomInfo"
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
import { createRoom, updateRoomProfile, getRoomInfo } from 'api/http/room'
import { ROLE } from 'configs/remote.config'
import { ROOM_STATUS, COMPANY_CODE } from 'configs/status.config'
import { TARGET_COMPANY } from 'configs/env.config'
import toastMixin from 'mixins/toast'
import confirmMixin from 'mixins/confirm'
import { maxParticipants } from 'utils/callOptions'
import { checkPermission } from 'utils/deviceCheck'

export default {
  name: 'WorkspaceCreateOpenRoom',
  mixins: [toastMixin, confirmMixin],
  components: {
    Modal,
    OpenRoomInfo,
  },
  data() {
    return {
      visibleFlag: false,
      maxSelect: maxParticipants - 1,
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
    selectUser(user) {
      const idx = this.selection.findIndex(select => user.uuid === select.uuid)
      if (idx < 0) {
        if (this.selection.length >= this.maxSelect) {
          this.toastNotify(this.$t('workspace.create_max_member'))
          return
        }
        this.selection.push(user)
      } else {
        this.selection.splice(idx, 1)
      }
    },
    async startRemote(info) {
      try {
        if (this.clicked === true) return
        this.clicked = true

        const options = await checkPermission()

        const createdRes = await createRoom({
          title: info.title,
          description: info.description,
          leaderId: this.account.uuid,
          sessionType: ROOM_STATUS.OPEN,
          participantIds: [],
          workspaceId: this.workspace.uuid,
          companyCode: COMPANY_CODE[TARGET_COMPANY],
        })
        if (info.imageFile) {
          updateRoomProfile({
            profile: info.imageFile,
            sessionId: createdRes.sessionId,
            uuid: this.account.uuid,
            workspaceId: this.workspace.uuid,
          })
        }
        const connRes = await this.$call.connect(
          createdRes,
          ROLE.LEADER,
          options,
        )

        const roomInfo = await getRoomInfo({
          sessionId: createdRes.sessionId,
          workspaceId: this.workspace.uuid,
        })
        window.urls['token'] = createdRes.token
        window.urls['coturn'] = createdRes.coturn
        window.urls['wss'] = createdRes.wss

        this.setRoomInfo({
          ...roomInfo,
          leaderId: this.account.uuid,
          open: true,
        })

        if (connRes) {
          this.clicked = false
          this.$eventBus.$emit('popover:close')

          this.$nextTick(() => {
            this.$router.push({ name: 'service' })
          })
        } else {
          this.roomClear()
          console.error('join room fail')
          this.clicked = false
        }
      } catch (err) {
        this.clicked = false
        if (typeof err === 'string') {
          if (err === 'nodevice') {
            this.toastError(this.$t('workspace.error_no_connected_device'))
          } else if (err.toLowerCase() === 'requested device not found') {
            this.toastError(this.$t('workspace.error_no_device'))
          } else if (err.toLowerCase() === 'device access deined') {
            this.$eventBus.$emit('devicedenied:show')
          }
        }
        this.roomClear()
        console.error(err)
      }
    },
  },

  /* Lifecycles */
  created() {},
  mounted() {},
}
</script>

<style lang="scss" src="assets/style/workspace/workspace-openroom.scss"></style>
