<template>
  <el-table
    class="coupon-list clickable"
    :data="coupons"
    @row-click="select"
    @sort-change="sortChange"
  >
    <ColumnDefault :label="$t('coupon.column.couponName')" prop="name" />
    <ColumnDefault
      :label="$t('coupon.column.couponNo')"
      prop="no"
      :width="180"
    />
    <ColumnDate
      :label="$t('coupon.column.registerDate')"
      prop="registerDate"
      :width="88"
    />
    <ColumnDate
      :label="$t('coupon.column.expireDate')"
      prop="expiredDate"
      :width="88"
    />
    <ColumnDate
      :label="$t('coupon.column.usedDate')"
      prop="usedDate"
      :width="88"
    />
    <ColumnStatus
      :label="$t('coupon.column.status')"
      prop="status"
      :statusList="couponStatus"
      :width="120"
    />
    <template #empty>
      <img src="~assets/images/empty/img-coupon.png" />
      <span>{{ $t('coupon.couponList.empty') }}</span>
    </template>
  </el-table>
</template>

<script>
import columnMixin from '@/mixins/columns'
import { status as couponStatus } from '@/models/coupon/Coupon'

export default {
  mixins: [columnMixin],
  props: {
    coupons: Array,
  },
  data() {
    return {
      couponStatus,
    }
  },
  methods: {
    select(row) {
      this.$emit('select', row)
    },
    sortChange(params) {
      this.$emit('sort-change', params)
    },
  },
}
</script>

<style lang="scss">
#__nuxt .el-card--table .el-card__body .coupon-list {
  &__body-wrapper {
    height: 640px;
  }
  td:last-child .cell {
    & > div {
      overflow: visible;
    }
    span.EXPIRED {
      background: rgba(178, 185, 201, 0.3);
      border: none;
      opacity: 0.7;
    }
    span.UNUSE {
      color: #186ae2;
      border-color: #186ae2;
    }
    span.USE {
      color: #727f9c;
      border-color: #727f9c;
      opacity: 0.8;
    }
  }
}
</style>
