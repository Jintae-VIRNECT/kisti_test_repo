import PlanMember from '@/models/purchases/PlanMember'

export default {
  searchPlanMembers() {
    const data = [0, 1, 2, 3, 4]
    return {
      list: data.map(member => new PlanMember(member)),
      total: data.length,
    }
  },
  getStorageCapacity() {
    return {
      used: 68.44,
      max: 100,
      remain: 31.55,
      default: 75,
      extend: 25,
    }
  },
}
