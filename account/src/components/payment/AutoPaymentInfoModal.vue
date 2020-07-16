<template>
  <el-dialog
    class="auto-payment-info-modal"
    :title="$t('payment.will.title')"
    :visible.sync="visible"
    width="580px"
    :before-close="handleClose"
  >
    <p>{{ $t('payment.autoPaymentInfoModal.desc') }}</p>
    <el-table :data="autoPaymentItems">
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
    <template slot="footer">
      <el-button @click="visible = false">
        {{ $t('common.cancel') }}
      </el-button>
      <a :href="$url.pay">
        <el-button type="primary">
          {{ $t('payment.autoPaymentInfoModal.submit') }}
        </el-button>
      </a>
    </template>
  </el-dialog>
</template>

<script>
import dialogMixin from '@/mixins/dialog'
import columnMixin from '@/mixins/columns'

export default {
  mixins: [dialogMixin, columnMixin],
  props: {
    autoPaymentItems: Array,
  },
}
</script>

<style lang="scss">
#__nuxt .auto-payment-info-modal {
}
</style>
