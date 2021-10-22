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
      // 마스터 권한이 아니라면
      if (this.activeWorkspace.role !== 'MASTER') return false

      // 편집할 멤버의 권한이 마스터라면
      if (role === 'MASTER') return false
      return true
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
    isUserTypeGuest(type) {
      return type === 'GUEST_USER'
    },
    isUserTypeWorkspaceOnly(type) {
      return type === 'WORKSPACE_ONLY_USER'
    },
  },
}
