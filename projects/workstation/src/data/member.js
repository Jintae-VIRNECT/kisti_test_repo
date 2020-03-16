import api from '@/api/gateway'

export default {
  async getMemberList() {
    return await api('MEMBER_LIST', {
      route: {
        workspaceId: '4d6eab0860969a50acbfa4599fbb5ae8',
      },
    })
  },
}
