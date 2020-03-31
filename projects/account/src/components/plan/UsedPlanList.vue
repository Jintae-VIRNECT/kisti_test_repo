<template>
  <div class="used-plan-list">
    <el-table :data="usedPlans">
      <column-plan
        label="플랜 이름"
        nameProp="name"
        gradeProp="grade"
        :width="200"
      />
      <column-default label="사용 중인 워크스페이스" prop="workspace" />
      <column-default label="플랜 만료일" prop="expireDate" :width="100" />
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
      usedPlans: planService.getUsedPlanList(),
    }
  },
}
</script>
