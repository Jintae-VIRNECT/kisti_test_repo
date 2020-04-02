<template>
  <div class="workspace-plan-list">
    <el-table :data="workspacePlans">
      <column-plan
        :label="$t('plan.column.planName')"
        nameProp="name"
        gradeProp="grade"
      />
      <column-default
        :label="$t('plan.column.usingMember')"
        prop="member"
        :width="200"
      />
      <column-default
        :label="$t('plan.column.purchasedDate')"
        prop="buyDate"
        :width="100"
      />
      <column-default
        :label="$t('plan.column.expireDate')"
        prop="expireDate"
        :width="100"
      />
    </el-table>
  </div>
</template>

<script>
import planService from '@/services/plan'

import ColumnDefault from '@/components/common/tableColumn/ColumnDefault'
import ColumnPlan from '@/components/common/tableColumn/ColumnPlan'

export default {
  components: {
    ColumnDefault,
    ColumnPlan,
  },
  data() {
    return {
      workspacePlans: planService.getWorkspacePlanList(),
    }
  },
}
</script>
