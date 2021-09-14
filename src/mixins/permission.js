import { mapGetters } from 'vuex'
export default {
  computed: {
    ...mapGetters({
      activeWorkspace: 'auth/activeWorkspace',
      plansInfo: 'plan/plansInfo',
      auth: 'auth/auth',
    }),
  },
  methods: {
    mine(userId) {
      return userId === this.auth.myInfo.uuid
    },
    canManage(role) {
      if (this.activeWorkspace.role === 'MANAGER') {
        if (role !== 'MASTER') return true
        else return false
      } else if (this.activeWorkspace.role === 'MASTER') return true
      else return false
    },
    isRoleMaster(role) {
      return role === 'MASTER'
    },
    isRoleManager(role) {
      return role === 'MANAGER'
    },
    isRoleGuest(role) {
      return role === 'GUEST'
    },
    isUserTypeUser(type) {
      return type === 'USER'
    },
    isUserTypeSeat(type) {
      return type === 'GUEST_USER'
    },
    isUserTypeWorkspaceOnly(type) {
      return type === 'WORKSPACE_ONLY_USER'
    },
  },
}
