import Plan from '@/models/plan'

export default {
  async getUsedPlanList() {
    const data = [0, 1, 2, 3, 4]
    return data.map(plan => new Plan(plan))
  },
}
