<template>
  <div>
    <el-table ref="table" :data="planMembers">
      <column-plan
        :label="$t('purchases.planMembersInfo.column.name')"
        prop="productName"
        productProp="productName"
        gradeProp="licenseType"
      />
      <column-user
        :label="$t('purchases.planMembersInfo.column.user')"
        prop="uuid"
        nameProp="nickname"
        imageProp="profile"
        :width="140"
      />
    </el-table>
    <searchbar-page
      ref="page"
      :value.sync="planMembersPage"
      :total="planMembersTotal"
    />
  </div>
</template>

<script>
import columnMixin from '@/mixins/columns'
import searchMixin from '@/mixins/search'
import purchasesService from '@/services/purchases'

export default {
  mixins: [columnMixin, searchMixin],
  data() {
    return {
      planMembers: [],
      planMembersTotal: 0,
      planMembersPage: 1,
    }
  },
  methods: {
    changedSearchParams(searchParams) {
      this.searchPlanMembers(searchParams)
    },
    async searchPlanMembers(searchParams) {
      const { list, total } = await purchasesService.searchPlanMembers(
        searchParams,
      )
      this.planMembers = list
      this.planMembersTotal = total
    },
  },
  beforeMount() {
    this.searchPlanMembers()
  },
}
</script>
