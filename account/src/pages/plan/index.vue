<template>
  <div id="plan" class="virnect-card">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>계정 관리</el-breadcrumb-item>
          <el-breadcrumb-item>사용 중인 플랜</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>사용 중인 플랜</h2>
        <p>내가 사용 중인 플랜 정보를 확인하고, 관리합니다.</p>
      </div>
      <el-row>
        <el-col class="container__left">
          <el-card class="el-card--table">
            <div slot="header">
              <h3>사용 중인 플랜 수</h3>
            </div>
            <subscribe-payment-info />
          </el-card>
        </el-col>
        <el-col class="container__right">
          <el-card class="el-card--table">
            <div slot="header">
              <h3>사용 중인 플랜</h3>
            </div>
            <using-plan-list @select="planSelect" />
          </el-card>
        </el-col>
      </el-row>

      <plan-detail-modal :data="planInfo" :visible.sync="planDetailVisible" />
    </div>
  </div>
</template>

<script>
import planService from '@/services/plan'

import SubscribePaymentInfo from '@/components/payment/SubscribePaymentInfo'
import UsingPlanList from '@/components/plan/UsingPlanList'
import PlanDetailModal from '@/components/plan/PlanDetailModal'

export default {
  components: {
    SubscribePaymentInfo,
    UsingPlanList,
    PlanDetailModal,
  },
  data: function() {
    return {
      planDetailVisible: false,
      planInfo: {},
    }
  },
  methods: {
    planSelect(row) {
      this.planInfo = planService.getPlanDetail(row)
      this.planDetailVisible = true
    },
  },
}
</script>

<style lang="scss"></style>
