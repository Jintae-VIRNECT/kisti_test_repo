<template>
  <div class="used-plan-list">
    <el-table :data="usingPlans" @row-click="select">
      <column-plan
        :label="$t('plan.column.planName')"
        nameProp="name"
        gradeProp="grade"
        :width="200"
      />
      <column-default
        :label="$t('plan.column.usingWorkspace')"
        prop="workspace"
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
      usingPlans: planService.getusingPlanList(),
    }
  },
  methods: {
    select(row) {
      this.$emit('select', row)
    },
  },
}
</script>
