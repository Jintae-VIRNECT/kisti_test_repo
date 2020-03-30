import UsedPlan from '@/models/plan/UsedPlan'
import WorkspacePlan from '@/models/plan/WorkspacePlan'

export default {
  getUsedPlanList() {
    const data = [0, 1, 2, 3, 4]
    return data.map(plan => new UsedPlan(plan))
  },
  getWorkspacePlanList() {
    const data = [0, 1, 2, 3, 4]
    return data.map(plan => new WorkspacePlan(plan))
  },
}
