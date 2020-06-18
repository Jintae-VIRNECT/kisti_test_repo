<template>
  <div id="purchases" class="virnect-card">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>{{ $t('menu.account') }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{ $t('menu.purchases') }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('menu.purchases') }}</h2>
        <p>{{ $t('purchases.desc') }}</p>
      </div>
      <el-row>
        <el-col class="container__left">
          <el-card>
            <div slot="header">
              <h3>{{ $t('purchases.info.title') }}</h3>
            </div>
            <purchases-info />
          </el-card>
        </el-col>
        <el-col class="container__right">
          <!-- 워크스페이스 플랜 사용자 정보 -->
          <el-card class="el-card--table">
            <div slot="header">
              <h3>{{ $t('purchases.planMembersInfo.title') }}</h3>
            </div>
            <el-table :data="planMembers">
              <column-plan
                :label="$t('purchases.planMembersInfo.column.name')"
                prop="product"
                productProp="product"
                gradeProp="grade"
              />
              <column-user
                :label="$t('purchases.planMembersInfo.column.user')"
                prop="memberName"
                nameProp="memberName"
                imageProp="memberProfile"
                :width="140"
              />
            </el-table>
            <el-row type="flex" justify="center">
              <el-pagination
                layout="prev, pager, next"
                :total="planMembersTotal"
                @current-change="searchPlanMembers"
              >
              </el-pagination>
            </el-row>
          </el-card>
          <!-- 사용량 -->
          <purchases-used />
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import purchasesService from '@/services/purchases'
import purchasesInfo from '@/components/purchases/PurchasesInfo'
import PurchasesUsed from '@/components/purchases/PurchasesUsed'
import columnMixin from '@/mixins/columns'

export default {
  mixins: [columnMixin],
  components: {
    purchasesInfo,
    PurchasesUsed,
  },
  async asyncData() {
    const { list, total } = await purchasesService.searchPlanMembers()
    return {
      planMembers: list,
      planMembersTotal: total,
    }
  },
  methods: {
    async searchPlanMembers() {
      const { list, total } = await purchasesService.searchPlanMembers()
      this.planMembers = list
      this.planMembersTotal = total
    },
  },
}
</script>

<style lang="scss">
#__nuxt #purchases {
  .container__right.el-col-24 {
    width: 830px;
  }
  .el-table__body-wrapper {
    min-height: 512px;
  }
  .el-progress-bar__inner {
    background: #007cfe;
  }
}
</style>
