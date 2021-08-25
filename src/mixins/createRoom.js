import { getMemberList } from 'api/http/member'
import { getHistorySingleItem } from 'api/http/history'

import toastMixin from 'mixins/toast'
import responsiveModalVisibleMixin from 'mixins/responsiveModalVisible'
import { mapActions } from 'vuex'

export default {
  mixins: [toastMixin, responsiveModalVisibleMixin],
  watch: {
    async visible(flag) {
      if (flag) {
        this.selection = []
        this.selectHistory = []
        if (this.sessionId && this.sessionId.length > 0) {
          await this.getInfo()
        }
        this.inviteRefresh()
      }
      this.setVisiblePcOrMobileFlag(flag)
    },
  },
  methods: {
    beforeClose() {
      this.$emit('update:visible', false)
    },
    ...mapActions(['setRoomInfo', 'roomClear']),
    async getInfo() {
      try {
        this.roomInfo = await getHistorySingleItem({
          workspaceId: this.workspace.uuid,
          sessionId: this.sessionId,
        })
        this.selectHistory = this.roomInfo.memberList.filter(
          member => member.uuid !== this.account.uuid,
        )
      } catch (err) {
        console.error(err)
      }
    },
    async inviteRefresh() {
      this.loading = true
      const inviteList = await getMemberList({
        size: 50,
        workspaceId: this.workspace.uuid,
        userId: this.account.uuid,
      })
      this.users = inviteList.memberList
      this.users.sort((A, B) => {
        if (A.role === 'MASTER') {
          return -1
        } else if (B.role === 'MASTER') {
          return 1
        } else if (A.role === 'MANAGER' && B.role !== 'MANAGER') {
          return -1
        } else {
          return 0
        }
      })
      this.selection = this.users.filter(
        user =>
          this.selectHistory.findIndex(history => history.uuid === user.uuid) >
          -1,
      )
      this.loading = false
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
  },
}
