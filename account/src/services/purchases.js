import PlanMember from '@/models/purchases/PlanMember'

export default {
  searchPlanMembers() {
    const data = [0, 1, 2, 3, 4]
    return {
      list: data.map(member => new PlanMember(member)),
      total: data.length,
    }
  },
}
