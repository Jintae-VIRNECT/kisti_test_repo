<template>
  <div>
    <el-table :data="workspacePlans">
      <column-plan label="플랜 이름" nameProp="name" gradeProp="grade" />
      <column-default label="사용 멤버" prop="member" :width="200" />
      <column-default label="플랜 구매일" prop="buyDate" :width="100" />
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
      workspacePlans: planService.getWorkspacePlanList(),
    }
  },
}
</script>
