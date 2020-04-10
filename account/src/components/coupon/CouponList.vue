<template>
  <div class="coupon-list">
    <el-table :data="coupons" @row-click="select">
      <column-default :label="$t('coupon.column.couponName')" prop="name" />
      <column-default
        :label="$t('coupon.column.registerDate')"
        prop="registerDate"
        :width="200"
      />
      <template #empty>
        <img src="~assets/images/empty/img-coupon.png" />
        <span>{{ $t('coupon.couponList.empty') }}</span>
      </template>
    </el-table>
  </div>
</template>

<script>
import couponService from '@/services/coupon'

import ColumnDefault from '@/components/common/tableColumn/ColumnDefault'

export default {
  components: {
    ColumnDefault,
  },
  data() {
    return {
      coupons: couponService.getCouponList(),
    }
  },
  methods: {
    select(row) {
      this.$emit('select', row)
    },
  },
}
</script>

<style lang="scss">
.coupon-list {
  .el-table__body-wrapper {
    height: 640px;
  }
}
</style>
