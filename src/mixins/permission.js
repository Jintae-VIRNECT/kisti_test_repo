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
    canKick(type, role) {
      if (this.canManage(role)) {
        // 일반 계정 일 때 내보내기
        if (this.userTypeIsUser(type)) return true
        else return false
      }
      return false
    },
    roleIsMaster(role) {
      return role === 'MASTER'
    },
    roleIsManager(role) {
      return role === 'MANAGER'
    },
    roleIsSeat(role) {
      return role === 'SEAT'
    },
    userTypeIsUser(type) {
      return type === 'USER'
    },
    userTypeIsSeat(type) {
      return type === 'SEAT_USER'
    },
    userTypeIsWorkspaceOnlyUser(type) {
      return type === 'WORKSPACE_ONLY_USER'
    },
  },
}
