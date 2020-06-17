<template>
  <el-table :data="plans">
    <column-plan
      :label="$t('workspace.usingPlanList.column.name')"
      prop="planName"
      productProp="product"
      gradeProp="grade"
      :width="210"
      sortable
    />
    <column-user
      :label="$t('workspace.usingPlanList.column.workspaceName')"
      prop="workspaceUUID"
      nameProp="workspaceName"
      imageProp="workspaceProfile"
      sortable
    />
    <column-date
      :label="$t('workspace.usingPlanList.column.renewalDate')"
      prop="renewalDate"
      :width="120"
      sortable
    />
  </el-table>
</template>

<script>
import columnMixin from '@/mixins/columns'

export default {
  mixins: [columnMixin],
  props: {
    plans: Array,
  },
}
</script>
