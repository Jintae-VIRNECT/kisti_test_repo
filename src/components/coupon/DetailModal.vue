<template>
  <el-dialog
    class="coupon-detail-modal"
    :title="$t('coupon.detailModal.title')"
    :visible.sync="visible"
    width="628px"
    :before-close="handleClose"
  >
    <el-row>
      <el-col :span="8">
        <dt>{{ $t('coupon.detailModal.couponName') }}</dt>
        <dd>{{ coupon.name }}</dd>
        <dt>{{ $t('coupon.detailModal.couponNo') }}</dt>
        <dd>{{ coupon.no }}</dd>
        <dt>{{ $t('coupon.detailModal.couponType') }}</dt>
        <dd>{{ coupon.typeName }}</dd>
      </el-col>
      <el-col :span="16">
        <dt>{{ $t('coupon.detailModal.registerDate') }}</dt>
        <dd>{{ coupon.registerDate }}</dd>
        <dt>{{ $t('coupon.detailModal.usedDate') }}</dt>
        <dd>
          {{ coupon.usedDate || '-' }}
          <span class="caution">
            {{ $t('coupon.detailModal.usedDateDesc') }}
          </span>
        </dd>
        <dt>{{ $t('coupon.detailModal.expireDate') }}</dt>
        <dd>
          {{ coupon.expiredDate }}
          <span class="caution">
            {{ $t('coupon.detailModal.expireDateDesc') }}
          </span>
        </dd>
        <el-divider />
        <dt>{{ $t('coupon.detailModal.target') }}</dt>
        <dd>
          <span class="caution">
            {{ $t('coupon.detailModal.targetDesc') }}
          </span>
          <el-table :data="items" :show-header="false">
            <ColumnPlan
              :label="$t('payment.autoPaymentCancelModal.column.product')"
              :prop="$i18n.locale === 'ko' ? 'name' : 'nameEng'"
              productProp="product"
            />
          </el-table>
        </dd>
        <el-divider />
        <div>
          <p>{{ $t('coupon.detailModal.desc') }}</p>
          <a :href="$url.pay">
            <el-button type="primary">
              {{ $t('common.payCenter') }}
            </el-button>
          </a>
        </div>
      </el-col>
    </el-row>
  </el-dialog>
</template>

<script>
import dialogMixin from '@/mixins/dialog'
import columnMixin from '@/mixins/columns'

export default {
  mixins: [dialogMixin, columnMixin],
  props: {
    allTickets: Array,
    coupon: Object,
  },
  computed: {
    items() {
      return (!this.coupon.applyItemId
        ? this.allTickets
        : this.allTickets.filter(
            ticket => ticket.id === this.coupon.applyItemId,
          )
      ).map(ticket => {
        if (ticket.productType.id === 'product') {
          ticket.product = ticket.productType.name.toLowerCase()
        }
        return ticket
      })
    },
  },
}
</script>

<style lang="scss">
#__nuxt .coupon-detail-modal {
  .el-col:nth-child(2) {
    max-height: 520px;
    padding-left: 20px;
    overflow-y: auto;
  }
  dt {
    color: $font-color-desc;
    font-size: 12px;
    line-height: 20px;
  }
  dd {
    margin: 4px 0 20px;
    .caution {
      display: block;
      margin-top: 4px;
      color: #8c8e92;
      font-size: 12px;
    }
  }
  .el-table {
    margin: 12px 0;
  }
  .el-table td {
    height: 28px;
    padding: 4px 0;
    font-size: 13px;
    border-bottom: none;
    img {
      width: 28px;
      height: 28px;
    }
  }
  .el-table--border::after,
  .el-table--group::after,
  .el-table::before {
    display: none;
  }
  p {
    margin: 48px 0 16px;
    color: $font-color-desc;
    font-size: 12px;
    line-height: 18px;
  }
  .el-button {
    width: 100%;
  }
}
</style>
