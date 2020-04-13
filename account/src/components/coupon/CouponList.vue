<template>
  <div class="coupon-list">
    <el-table :data="coupons" @row-click="select" @sort-change="sort">
      <column-default
        :label="$t('coupon.column.couponName')"
        prop="name"
        :sortable="true"
      />
      <column-date
        :label="$t('coupon.column.registerDate')"
        prop="registerDate"
        :width="88"
        :sortable="true"
      />
      <column-date
        :label="$t('coupon.column.expireDate')"
        prop="expireDate"
        :width="88"
        :sortable="true"
      />
      <column-date
        :label="$t('coupon.column.usedDate')"
        prop="startDate"
        prop2="endDate"
        :width="160"
        :sortable="true"
      />
      <column-status
        :label="$t('coupon.column.status')"
        prop="status"
        statusList="coupon.status"
        :width="120"
        :sortable="true"
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
import ColumnDate from '@/components/common/tableColumn/ColumnDate'
import ColumnStatus from '@/components/common/tableColumn/ColumnStatus'

export default {
  components: {
    ColumnDefault,
    ColumnDate,
    ColumnStatus,
  },
  data() {
    return {
      coupons: [],
    }
  },
  methods: {
    select(row) {
      this.$emit('select', row)
    },
    sort(column) {
      console.log(column)
    },
  },
  async beforeCreate() {
    this.coupons = await couponService.getCouponList()
  },
}
</script>

<style lang="scss">
.coupon-list {
  .el-table {
    &__body-wrapper {
      height: 640px;
    }
    td:last-child .cell {
      overflow: visible;
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
}
</style>
