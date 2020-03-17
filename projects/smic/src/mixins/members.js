import { mapGetters } from 'vuex'
export default {
  computed: {
    ...mapGetters(['allMembersList']),
  },
  methods: {
    uuidToMember(uuid) {
      return this.allMembersList.find(member => member.uuid === uuid) || {}
    },
    uuidsToMembers(uuids) {
      return uuids.map(worker => {
        return this.allMembersList.find(
          member => member.uuid === worker.workerUUID,
        ).name
      })
    },
  },
}
