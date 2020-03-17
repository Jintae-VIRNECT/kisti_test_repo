import memberData from '@/data/member'

export default {
  async getDefaultMemberList() {
    const data = await memberData.getMemberList({
      userId: '498b1839dc29ed7bb2ee90ad6985c608',
      size: 10,
      sort: 'name,asc',
    })
    return data.memberInfoList
  },
}
