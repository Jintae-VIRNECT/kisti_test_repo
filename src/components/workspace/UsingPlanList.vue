<template>
  <div>
    <el-table ref="table" :data="plans">
      <ColumnPlan
        :label="$t('workspace.usingPlanList.column.name')"
        prop="planName"
        productProp="product"
        gradeProp="grade"
        :width="210"
        sortable
      />
      <ColumnUser
        type="no-tooltip"
        :label="$t('workspace.usingPlanList.column.workspaceName')"
        prop="workspaceUUID"
        nameProp="workspaceName"
        imageProp="workspaceProfile"
        :defaultImage="$defaultWorkspaceProfile"
        sortable
      />
      <ColumnDate
        :label="$t('workspace.usingPlanList.column.renewalDate')"
        prop="renewalDate"
        :width="130"
        sortable
      />
    </el-table>
    <SearchbarPage
      v-if="!isHome"
      ref="page"
      :value.sync="plansPage"
      :pageSize="6"
      :total="plansTotal"
    />
  </div>
</template>

<script>
import columnMixin from '@/mixins/columns'
import searchMixin from '@/mixins/search'
import workspaceService from '@/services/workspace'

export default {
  mixins: [columnMixin, searchMixin],
  props: {
    isHome: Boolean,
  },
  data() {
    return {
      plans: [],
      plansPage: 1,
      plansTotal: 0,
    }
  },
  methods: {
    changedSearchParams(searchParams) {
      this.searchUsingPlans(searchParams)
    },
    async searchUsingPlans(searchParams) {
      const { list, total } = await workspaceService.searchUsingPlans(
        searchParams,
      )
      this.plans = list
      this.plansTotal = total
    },
  },
  beforeMount() {
    this.searchUsingPlans({
      size: this.isHome ? 5 : 6,
    })
  },
}
</script>
