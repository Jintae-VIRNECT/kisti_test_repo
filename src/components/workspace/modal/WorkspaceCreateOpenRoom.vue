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
import { mapGetters, mapActions } from 'vuex'
import OpenRoomInfo from '../partials/ModalCreateOpenRoomInfo'

import { getHistorySingleItem } from 'api/http/history'
import {
  createRoom,
  restartRoom,
  updateRoomProfile,
  getRoomInfo,
} from 'api/http/room'
import { ROLE } from 'configs/remote.config'
import { ROOM_STATUS } from 'configs/status.config'
import toastMixin from 'mixins/toast'
import confirmMixin from 'mixins/confirm'
import callMixin from 'mixins/call'

export default {
  name: 'WorkspaceCreateOpenRoom',
  mixins: [toastMixin, confirmMixin, callMixin],
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
  },
  computed: {
    ...mapGetters(['targetCompany']),
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
    async startRemote(info) {
      try {
        if (this.clicked === true) return
        this.clicked = true

        const options = await this.getDeviceId()
        const mediaStream = await this.$call.getStream(options)

        let createdRes
        if (this.sessionId && this.sessionId.length > 0) {
          createdRes = await restartRoom({
            client: 'DESKTOP',
            userId: this.account.uuid,
            title: info.title,
            description: info.description,
            leaderId: this.account.uuid,
            participantIds: [],
            workspaceId: this.workspace.uuid,
            sessionId: this.sessionId,
            sessionType: ROOM_STATUS.OPEN,
            companyCode: this.targetCompany,
          })
        } else {
          createdRes = await createRoom({
            client: 'DESKTOP',
            userId: this.account.uuid,
            title: info.title,
            description: info.description,
            leaderId: this.account.uuid,
            sessionType: ROOM_STATUS.OPEN,
            participantIds: [],
            workspaceId: this.workspace.uuid,
            companyCode: this.targetCompany,
          })
        }
        if (info.imageFile) {
          await updateRoomProfile({
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
          mediaStream,
        )

        const roomInfo = await getRoomInfo({
          sessionId: createdRes.sessionId,
          workspaceId: this.workspace.uuid,
        })

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
        this.roomClear()
        if (typeof err === 'string') {
          console.error(err)
          if (err === 'nodevice') {
            this.toastError(this.$t('workspace.error_no_connected_device'))
            return
          } else if (err.toLowerCase() === 'requested device not found') {
            this.toastError(this.$t('workspace.error_no_device'))
            return
          } else if (err.toLowerCase() === 'device access deined') {
            this.$eventBus.$emit('devicedenied:show')
            return
          }
        } else if (err.code === 7003) {
          this.toastError(this.$t('service.file_type'))
        } else if (err.code === 7004) {
          this.toastError(this.$t('service.file_maxsize'))
        } else {
          console.error(`${err.message} (${err.code})`)
          this.toastError(this.$t('confirm.network_error'))
        }
      }
    },
  },

  /* Lifecycles */
  created() {},
  mounted() {},
}
</script>

<style lang="scss" src="assets/style/workspace/workspace-openroom.scss"></style>
