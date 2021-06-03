<template>
  <el-card class="auto-payment-info">
    <div slot="header">
      <h3>{{ $t('payment.will.title') }}</h3>
    </div>
    <dl class="horizon">
      <dt>{{ $t('payment.will.price') }}</dt>
      <dd class="price">
        <span>{{ autoPayments.price.toLocaleString() }}</span>
        <span>{{ $t('payment.monetaryUnit') }}</span>
      </dd>
      <el-divider />
      <dt>{{ $t('payment.will.dueDate') }}</dt>
      <dd>{{ autoPayments.nextPayDate | fullYearDateFormat }}</dd>
      <el-divider />
      <dt>{{ $t('payment.will.way') }}</dt>
      <dd v-if="autoPayments.payFlag === 'Y'">
        <span>{{ autoPayments.way }}</span>
        <span class="sub">
          {{ $t('payment.will.autoPaymentEveryMonth') }}
        </span>
      </dd>
      <dd v-else>-</dd>
    </dl>
    <el-divider />
    <h5>{{ $t('payment.info.title') }}</h5>
    <dl class="info horizon">
      <dt>{{ $t('payment.info.totalAmount') }}</dt>
      <dd class="blue">{{ totalProductsAmount }}</dd>
      <div class="plans">
        <dd v-for="product in autoPayments.products" :key="product.value">
          <div class="column-plan">
            <img :src="product.logo" />
            <span>{{ product.label }}</span>
          </div>
          <div class="count">
            <span>{{ product.amount }}</span>
          </div>
        </dd>
      </div>
      <dt>{{ $t('purchases.info.arStorageCapacity') }}</dt>
      <dd>{{ autoPayments.storage.default.toLocaleString() }} GB</dd>
      <dt>{{ $t('purchases.info.arContentsViewCount') }}</dt>
      <dd>
        {{ autoPayments.viewCount.default.toLocaleString() }}
        {{ $t('purchases.countsUnit') }}
      </dd>
      <dt>{{ $t('purchases.info.callTime') }}</dt>
      <dd>
        {{ $t('purchases.infinity') }}
      </dd>
    </dl>
    <el-divider />
    <h5>{{ $t('payment.info.extend') }}</h5>
    <dl class="info horizon">
      <dt>{{ $t('purchases.info.arStorageCapacity') }}</dt>
      <dd class="blue">{{ autoPayments.storage.add.toLocaleString() }} GB</dd>
      <dt>{{ $t('purchases.info.arContentsViewCount') }}</dt>
      <dd class="blue">
        {{ autoPayments.viewCount.add.toLocaleString() }}
        {{ $t('purchases.countsUnit') }}
      </dd>
    </dl>

    <el-divider />
    <div class="button-wrapper">
      <!-- 플랜 및 서비스 이용권 변경 -->
      <el-button type="primary" class="wide" @click="openPayCenter">
        {{ $t('purchases.info.changePlans') }}
      </el-button>
      <!-- 자동 결제 해지 신청 취소 -->
      <el-button
        v-if="autoPayments.payFlag === 'N'"
        type="simple"
        class="wide"
        @click="autoPaymentAbort"
      >
        {{ $t('payment.autoPaymentCancelModal.cancelRequestCancel') }}
      </el-button>
      <!-- 자동 결제 해지 -->
      <el-button
        v-else-if="autoPayments.payFlag === 'Y'"
        type="simple"
        class="wide"
        @click="showAutoPaymentCancelModal = true"
      >
        {{ $t('payment.autoPaymentCancelModal.title') }}
      </el-button>
    </div>

    <!-- 모달 -->
    <PaymentCancelModal
      :autoPaymentId="autoPayments.id"
      :autoPaymentItems="autoPayments.items"
      :visible.sync="showAutoPaymentCancelModal"
      @updated="autoPaymentsCancled"
    />
  </el-card>
</template>

<script>
import filterMixin from '@/mixins/filters'
import paymentService from '@/services/payment'
import purchaseService from '@/services/purchases'

export default {
  mixins: [filterMixin],
  data() {
    return {
      showAutoPaymentCancelModal: false,
      autoPayments: {
        price: '',
        items: [],
        products: [],
        storage: { default: '', add: '' },
        viewCount: { default: '', add: '' },
      },
    }
  },
  computed: {
    totalProductsAmount() {
      return this.autoPayments.products.reduce((p, n) => p + n.amount, 0)
    },
  },
  methods: {
    openPayCenter() {
      window.open(this.$url.pay)
    },
    // 해지됨
    async autoPaymentsCancled() {
      this.autoPayments = await paymentService.getAutoPayments()
    },
    // 해지 신청 취소
    autoPaymentAbort() {
      this.$confirm(
        this.$t('payment.autoPaymentCancelModal.cancelRequestCancelDesc'),
        this.$t('payment.autoPaymentCancelModal.cancelRequestCancel'),
      ).then(async () => {
        try {
          await paymentService.cancelAutoPaymentsAbort(this.autoPayments.id)
          this.$notify.success({
            message: this.$t(
              'payment.autoPaymentCancelModal.cancelRequestCancelDone',
            ),
            position: 'bottom-left',
            duration: 2000,
          })
          this.autoPayments = await paymentService.getAutoPayments()
        } catch (e) {
          this.$notify.error({
            message: e,
            position: 'bottom-left',
            duration: 2000,
          })
        }
      })
    },
  },
  async beforeCreate() {
    this.plansInfo = await purchaseService.getWorkspacePlansInfo()
    this.autoPayments = await paymentService.getAutoPayments()
  },
}
</script>

<style lang="scss">
.auto-payment-info {
  h5 {
    margin-bottom: 16px;
  }
  .button-wrapper {
    margin: 30px 0 10px;
  }
  .el-button.wide {
    width: 100%;
    height: 36px;
    font-size: 14px;

    + .el-button.wide {
      margin: 10px 0 0;
    }
  }
  dl.horizon {
    dt {
      float: left;
    }
    dd {
      text-align: right;
    }
    .price {
      padding-top: 28px;
      & > span:first-child {
        color: #0b5bd8;
        font-size: 24px;
        line-height: 28px;
      }
    }
    .sub {
      display: block;
      color: $font-color-desc;
      font-size: 13px;
    }
    & + .el-button {
      margin: 30px 0 10px;
    }
    & + a {
      display: block;
      margin: 60px 0 10px;
    }

    &.info {
      font-size: 13px;
      dt {
        color: $font-color-desc;
      }
      dd {
        margin-bottom: 16px;
      }
      .blue {
        color: $color-primary;
      }
    }
  }
  .way {
    dd {
      color: $font-color-desc;
      font-size: 12px;
    }
    p {
      margin: 8px 0 16px;
      line-height: 20px;
    }
    .el-button {
      margin: 10px 0;
    }
  }
  // 모달 테이블
  .el-dialog .el-table {
    margin: 24px 0;
    .el-table__body-wrapper {
      margin: 8px 0;
    }
    thead tr {
      height: 40px;
    }
    .cell:first-child {
      padding-left: 0;
    }
    .cell:last-child {
      padding-right: 0;
    }
    .el-table__row {
      height: 56px;
    }
    th {
      padding: 8px 0;
      color: $font-color-desc;
      font-size: 12px;
    }
    td {
      color: $font-color-content;
      border-bottom: none;
    }
  }
  dl .plans {
    margin-bottom: 25px;
    dd {
      text-align: left;
      .column-plan {
        display: inline-block;
      }
      .count {
        float: right;
        line-height: 35px;
      }
    }
  }
}
</style>
