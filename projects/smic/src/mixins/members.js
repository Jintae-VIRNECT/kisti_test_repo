import { mapGetters } from 'vuex'
export default {
  computed: {
    ...mapGetters(['memberList']),
  },
  methods: {
    uuidToMember(uuid) {
      const memberList = this.memberList
      return memberList.find(member => member.uuid === uuid) || {}
    },
    uuidsToMembers(uuids) {
      return uuids.map(worker => {
        return this.memberList.find(member => member.uuid === worker.workerUUID)
          .name
      })
    },
  },
}
