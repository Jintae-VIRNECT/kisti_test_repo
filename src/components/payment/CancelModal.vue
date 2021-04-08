<template>
  <el-dialog
    class="auto-payment-cancel-modal"
    :title="$t('payment.autoPaymentCancelModal.title')"
    :visible.sync="visible"
    width="580px"
    :before-close="handleClose"
  >
    <p>{{ $t('payment.autoPaymentCancelModal.desc') }}</p>
    <el-table :data="autoPaymentItems">
      <ColumnPlan
        :label="$t('payment.autoPaymentCancelModal.column.product')"
        prop="name"
        productProp="product"
      />
      <ColumnCount
        :label="$t('payment.autoPaymentCancelModal.column.count')"
        :unit="$t('payment.autoPaymentCancelModal.column.numbersUnit')"
        prop="count"
        :width="70"
      />
      <ColumnPrice :label="$t('common.price')" prop="price" :width="90" />
    </el-table>
    <div class="caution">
      <h6>{{ $t('payment.autoPaymentCancelModal.cautionTitle') }}</h6>
      <p v-html="$t('payment.autoPaymentCancelModal.caution')" />
      <el-checkbox v-model="agree">
        {{ $t('payment.autoPaymentCancelModal.cautionAgree') }}
      </el-checkbox>
    </div>
    <template slot="footer">
      <el-button @click="handleClose">
        {{ $t('common.cancel') }}
      </el-button>
      <el-button
        type="primary"
        @click="autoPaymentCacnel"
        :disabled="!autoPaymentItems.length || !agree"
      >
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
  props: {
    autoPaymentId: Number,
    autoPaymentItems: Array,
  },
  data() {
    return {
      agree: false,
    }
  },
  methods: {
    opened() {
      this.agree = false
    },
    async autoPaymentCacnel() {
      try {
        await paymentService.cancelAutoPayments(this.autoPaymentId)
        this.$emit('updated')
        await this.$alert(
          this.$t('payment.autoPaymentCancelModal.submitDoneDesc'),
          this.$t('payment.autoPaymentCancelModal.submitDone'),
        )
        this.visible = false
      } catch (e) {
        this.$notify.error({
          message: e,
          position: 'bottom-left',
          duration: 2000,
        })
      }
    },
  },
}
</script>

<style lang="scss">
#__nuxt .auto-payment-cancel-modal {
  .caution {
    padding: 18px 20px;
    font-size: 13px;
    background: #f5f7fa;
    p {
      margin: 4px 0 20px;
      color: $font-color-desc;
      line-height: 20px;
    }
    .el-checkbox__label {
      font-size: 13px;
    }
  }
  .el-dialog__footer {
    padding-top: 0;
  }
}
</style>
