<template>
  <el-dialog
    class="auto-payment-cancel-modal"
    :title="$t('payment.way.autoPaymentCancel')"
    :visible.sync="visible"
    width="580px"
    :before-close="handleClose"
  >
    <p>{{ $t('payment.autoPaymentCancelModal.desc') }}</p>
    <el-table :data="autoPayments">
      <column-plan
        :label="$t('payment.autoPaymentCancelModal.column.product')"
        prop="name"
        productProp="product"
      />
      <column-count
        :label="$t('payment.autoPaymentCancelModal.column.count')"
        :unit="$t('payment.autoPaymentCancelModal.column.numbersUnit')"
        prop="count"
        :width="70"
      />
      <column-price :label="$t('common.price')" prop="price" :width="90" />
    </el-table>
    <p class="caution" v-html="$t('payment.autoPaymentCancelModal.caution')" />
    <template slot="footer">
      <el-button>{{ $t('common.delete') }}</el-button>
      <el-button type="primary">
        {{ $t('payment.autoPaymentCancelModal.submit') }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script>
import dialogMixin from '@/mixins/dialog'
import columnMixin from '@/mixins/columns'
import paymentService from '@/services/payment'

export default {
  mixins: [dialogMixin, columnMixin],
  data() {
    return {
      autoPayments: [],
    }
  },
  async beforeMount() {
    this.autoPayments = await paymentService.getAutoPayments()
  },
}
</script>

<style lang="scss">
#__nuxt .auto-payment-cancel-modal {
  .caution {
    margin-bottom: 46px;
    color: $font-color-desc;
    font-size: 12.6px;
  }
}
</style>
