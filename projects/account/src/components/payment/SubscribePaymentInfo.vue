<template>
  <div class="subscribe-payment-info">
    <el-table class="el-table--list" :data="PaymentScheduledPlans">
      <column-plan
        :label="$t('home.payment.willPaymentPlans')"
        nameProp="name"
        gradeProp="grade"
      />
      <column-default :label="total" prop="count" :width="60" align="right" />
    </el-table>
    <div class="subscribe-payment-info__storage">
      <span class="desc">{{ $t('home.payment.storageVolume') }}</span>
      <span class="display">
        <span class="current">{{ storage.current }}G</span>
        /{{ storage.max }}G
      </span>
      <el-progress :percentage="storagePercent" :show-text="false" />
    </div>
  </div>
</template>

<script>
import ColumnDefault from '@/components/common/tableColumn/ColumnDefault'
import ColumnPlan from '@/components/common/tableColumn/ColumnPlan'

export default {
  components: {
    ColumnDefault,
    ColumnPlan,
  },
  data() {
    return {
      PaymentScheduledPlans: [
        { name: 'Remote', grade: 'BASIC', count: 2 },
        { name: 'Make', grade: 'PRO', count: 4 },
        { name: 'Remote', grade: 'BASIC', count: 2 },
        { name: 'Make', grade: 'PRO', count: 4 },
        { name: 'Remote', grade: 'BASIC', count: 2 },
      ],
      total: '12',
      storage: {
        current: 10.5,
        max: 30,
      },
    }
  },
  computed: {
    storagePercent() {
      const { current, max } = this.storage
      return (current / max) * 100
    },
  },
}
</script>

<style lang="scss">
.subscribe-payment-info {
  margin: 22px 0;
}
.subscribe-payment-info__storage {
  margin: 30px 30px 50px;

  .desc {
    color: $font-color-desc;
    font-size: 12px;
  }
  .display {
    float: right;
    font-size: 13px;
  }
  .current {
    color: #0b5bd8;
  }
  .el-progress {
    margin: 16px 0;
  }
  .el-progress-bar__inner {
    background-color: #237df5;
  }
}
#__nuxt .el-card--table .subscribe-payment-info th:nth-last-child(2) {
  padding-bottom: 14px;
  color: $font-color-content;
  font-size: 16px;
}
</style>
