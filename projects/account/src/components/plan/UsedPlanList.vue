<template>
  <div>
    <el-table :data="usedPlans">
      <column-default label="플랜 이름" prop="name" />
      <column-default label="사용 중인 워크스페이스" prop="workspace" />
      <column-default label="플랜 만료일" prop="expire" />
    </el-table>
  </div>
</template>

<script>
import planService from '@/services/plan'

import ColumnDefault from '@/components/common/tableColumn/ColumnDefault'

export default {
  components: {
    ColumnDefault,
  },
  async asyncData() {
    return {
      usedPlans: await planService.getUsedPlanList(),
    }
  },
}
</script>

<style lang="scss"></style>
