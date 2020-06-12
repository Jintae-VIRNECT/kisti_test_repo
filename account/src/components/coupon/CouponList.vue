<template>
  <div class="coupon-list">
    <el-table
      class="clickable"
      :data="coupons"
      @row-click="select"
      @sort-change="sort"
    >
      <column-default
        :label="$t('coupon.column.couponName')"
        prop="name"
        sortable="custom"
      />
      <column-date
        :label="$t('coupon.column.registerDate')"
        prop="registerDate"
        :width="88"
        sortable="custom"
      />
      <column-date
        :label="$t('coupon.column.expireDate')"
        prop="expiredDate"
        :width="88"
        sortable="custom"
      />
      <column-date
        :label="$t('coupon.column.usedDate')"
        prop="startDate"
        prop2="endDate"
        :width="160"
        sortable="custom"
      />
      <column-status
        :label="$t('coupon.column.status')"
        prop="status"
        :statusList="couponStatus"
        :width="120"
        sortable="custom"
      />
      <template #empty>
        <img src="~assets/images/empty/img-coupon.png" />
        <span>{{ $t('coupon.couponList.empty') }}</span>
      </template>
    </el-table>
  </div>
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
    async select(row) {
      if (row.status === 'UNUSE') {
        this.$emit('select', row)
      } else if (row.status === 'USE') {
        this.$notify.error({
          message: this.$t('coupon.message.useAlready'),
          position: 'bottom-left',
          duration: 2000,
        })
      } else if (row.status === 'EXPIRED') {
        this.$notify.error({
          message: this.$t('coupon.message.useExpired'),
          position: 'bottom-left',
          duration: 2000,
        })
      }
    },
    sort(column) {
      this.$emit('sort', column)
    },
  },
}
</script>

<style lang="scss">
#__nuxt .coupon-list {
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
