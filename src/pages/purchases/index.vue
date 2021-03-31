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
              <h3>{{ $t('home.workspace.title') }}</h3>
              <i
                class="el-icon-caret-bottom"
                v-if="!showWorkspaceInfo"
                @click="showWorkspaceInfo = true"
              />
              <i
                class="el-icon-caret-top"
                v-if="showWorkspaceInfo"
                @click="showWorkspaceInfo = false"
              />
            </div>
            <WorkspaceInfo v-if="showWorkspaceInfo" />
          </el-card>
          <el-card>
            <div slot="header">
              <h3>{{ $t('purchases.info.title') }}</h3>
            </div>
            <PurchaseInfo :plansInfo="plansInfo" :paymentInfo="paymentInfo" />
          </el-card>
        </el-col>
        <el-col class="container__right">
          <!-- 워크스페이스 플랜 사용자 정보 -->
          <el-card class="el-card--table">
            <div slot="header">
              <h3>{{ $t('purchases.planMembersInfo.title') }}</h3>
            </div>
            <PurchasePlanMemberList />
          </el-card>
          <!-- 사용량 -->
          <PurchaseUsed :plansInfo="plansInfo" :paymentInfo="paymentInfo" />
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import purchaseService from '@/services/purchases'
import paymentService from '@/services/payment'
import columnMixin from '@/mixins/columns'

export default {
  mixins: [columnMixin],
  async asyncData() {
    const [plansInfo, paymentInfo] = await Promise.all([
      purchaseService.getWorkspacePlansInfo(),
      paymentService.getAutoPayments(),
    ])

    return { plansInfo, paymentInfo }
  },
  data() {
    return {
      showWorkspaceInfo: false,
    }
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
  .el-card__header i {
    float: right;
    margin-right: -8px;
    padding: 5px 0;
    cursor: pointer;
  }
}
</style>
