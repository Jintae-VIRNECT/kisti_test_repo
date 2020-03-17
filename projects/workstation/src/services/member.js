import api from '@/api/gateway'
import { Member } from '@/models/member'

export default {
  async getDefaultMembersList() {
    const data = await api('MEMBER_LIST', {
      route: {
        workspaceId: '4d6eab0860969a50acbfa4599fbb5ae8',
      },
      params: {
        userId: '498b1839dc29ed7bb2ee90ad6985c608',
        size: 20,
        sort: 'name,asc',
      },
    })
    return data.memberInfoList.map(member => Member(member))
  },
}
