<template>
  <div>
    <el-table ref="table" :data="plans">
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
    <searchbar-page
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
