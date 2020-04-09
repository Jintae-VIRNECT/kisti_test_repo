import UsedPlan from '@/models/plan/UsedPlan'
import WorkspacePlan from '@/models/plan/WorkspacePlan'

export default {
  getusingPlanList() {
    return []
    const data = [0, 1, 2, 3, 4]
    return data.map(plan => new UsedPlan(plan))
  },
  getWorkspacePlanList() {
    return []
    const data = [0, 1, 2, 3, 4]
    return data.map(plan => new WorkspacePlan(plan))
  },
  getPlanDetail(planId) {
    return new UsedPlan()
  },
}
