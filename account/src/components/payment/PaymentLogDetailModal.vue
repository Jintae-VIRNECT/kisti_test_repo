<template>
  <el-dialog
    class="payment-log-detail-modal"
    :visible.sync="visible"
    width="905px"
    top="10vh"
    :before-close="handleClose"
  >
    <template slot="title">
      <div class="el-dialog__title" v-if="!showCardSlip">
        <span>{{ $t('payment.creditCardSlip.title') }}</span>
      </div>
      <div class="el-dialog__title" v-else>
        <img
          @click="showCardSlip = false"
          src="~assets/images/icon/ic-arrow-back.svg"
        />
        <span v-if="!showCardSlip">{{ $t('payment.logDetail.title') }}</span>
        <span v-else>{{ $t('payment.creditCardSlip.title') }}</span>
      </div>
    </template>
    <el-row>
      <!-- 결제 정보 -->
      <el-col :span="8" v-if="!showCardSlip">
        <h6>{{ $t('payment.logDetail.info') }}</h6>
        <dl class="horizon">
          <dt>{{ $t('payment.log.column.paidDate') }}</dt>
          <dd>{{ paymentLogDetail.paidDate | dateFormat }}</dd>
          <dt>{{ $t('payment.log.column.way') }}</dt>
          <dd>
            <span>{{ paymentLogDetail.way }}</span>
            <span class="sub">
              {{ $t('payment.will.autoPaymentEveryMonth') }}
            </span>
          </dd>
          <dt>{{ $t('payment.log.column.no') }}</dt>
          <dd>{{ paymentLogDetail.no }}</dd>
        </dl>
        <el-divider />
        <el-button type="simple" class="wide" @click="showCardSlip = true">
          {{ $t('payment.logDetail.creditCardSlip') }}
        </el-button>
      </el-col>
      <!-- 카드 전표 -->
      <el-col class="card-slip" :span="8" v-if="showCardSlip">
        <h6>{{ $t('payment.creditCardSlip.paymentInfo') }}</h6>
        <dl class="horizon">
          <dt>{{ $t('payment.creditCardSlip.cardType') }}</dt>
          <dd>-</dd>
          <dt>{{ $t('payment.creditCardSlip.cardNo') }}</dt>
          <dd>-</dd>
          <dt>{{ $t('payment.creditCardSlip.tradeType') }}</dt>
          <dd>-</dd>
          <dt>{{ $t('payment.creditCardSlip.tradeDate') }}</dt>
          <dd>-</dd>
          <dt>{{ $t('payment.creditCardSlip.instalmentMonth') }}</dt>
          <dd>-</dd>
          <dt>{{ $t('payment.creditCardSlip.approvalNo') }}</dt>
          <dd>-</dd>
        </dl>
        <el-divider />
        <h6>{{ $t('payment.creditCardSlip.buyInfo') }}</h6>
        <dl class="horizon">
          <dt>{{ $t('payment.creditCardSlip.buyNo') }}</dt>
          <dd>-</dd>
          <dt>{{ $t('payment.creditCardSlip.buyUser') }}</dt>
          <dd>-</dd>
          <dt>{{ $t('payment.creditCardSlip.tax') }}</dt>
          <dd>-</dd>
          <dt>{{ $t('payment.creditCardSlip.taxFree') }}</dt>
          <dd>-</dd>
          <dt>{{ $t('payment.creditCardSlip.surtax') }}</dt>
          <dd>-</dd>
        </dl>
        <el-divider />
        <h6>{{ $t('payment.creditCardSlip.sellerInfo') }}</h6>
        <dl class="horizon">
          <dt>{{ $t('payment.creditCardSlip.sellerCompanyName') }}</dt>
          <dd>-</dd>
          <dt>{{ $t('payment.creditCardSlip.sellerCompanyNo') }}</dt>
          <dd>-</dd>
          <dt>{{ $t('payment.creditCardSlip.sellerAddress') }}</dt>
          <dd>-</dd>
        </dl>
        <el-divider />
        <el-button type="simple">
          {{ $t('common.download') }}
        </el-button>
        <el-button type="simple">
          {{ $t('common.print') }}
        </el-button>
      </el-col>
      <!-- 결제 상세 정보 -->
      <el-col :span="16">
        <h6>{{ $t('payment.logDetail.detail') }}</h6>
        <el-table :data="paymentLogDetail.tickets">
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
          <column-price
            :label="$t('payment.logDetail.priceWithVat')"
            prop="price"
            :width="90"
            align="right"
          />
        </el-table>
        <dl class="horizon">
          <dt>{{ $t('payment.logDetail.productPrice') }}</dt>
          <dd>{{ toPriceString(paymentLogDetail.price) }}</dd>
          <dt>{{ $t('payment.logDetail.vat') }}</dt>
          <dd>{{ toPriceString(paymentLogDetail.vat) }}</dd>
          <dt>{{ $t('payment.logDetail.discount') }}</dt>
          <dd>
            <span>{{ toPriceString(paymentLogDetail.discount) }}</span>
            <span class="sub">첫 구매 제품 플랜 10% 할인</span>
          </dd>
          <el-divider />
          <dt>{{ $t('payment.logDetail.period') }}</dt>
          <dd>
            {{ paymentLogDetail.startDate | fullYearDateFormat }}
            -
            {{ paymentLogDetail.endDate | fullYearDateFormat }}
          </dd>
          <el-divider />
          <dt>{{ $t('payment.logDetail.totalPayment') }}</dt>
          <dd class="price">
            <span>{{
              paymentLogDetail.total && paymentLogDetail.total.toLocaleString()
            }}</span>
            <span>{{ this.$t('payment.monetaryUnit') }}</span>
          </dd>
        </dl>
        <p v-if="showCardSlip">
          {{ this.$t('payment.creditCardSlip.caution') }}
        </p>
      </el-col>
    </el-row>
  </el-dialog>
</template>

<script>
import dialogMixin from '@/mixins/dialog'
import columnMixin from '@/mixins/columns'
import filterMixin from '@/mixins/filters'
import paymentService from '@/services/payment'

export default {
  mixins: [dialogMixin, columnMixin, filterMixin],
  data() {
    return {
      paymentLogDetail: {},
      showCardSlip: false,
    }
  },
  methods: {
    opened() {
      this.showCardSlip = false
    },
    download(url) {
      window.open(url)
    },
    print(url) {
      const popup = window.open('', '_blank')
      popup.document.write(`<img src="${url}" />`)
      popup.document.close()
      setTimeout(() => popup.print(), 1)
    },
  },
  async beforeMount() {
    this.paymentLogDetail = await paymentService.getPaymentLogDetail()
  },
}
</script>

<style lang="scss">
#__nuxt .payment-log-detail-modal {
  .el-dialog__title {
    & > * {
      display: inline-block;
      vertical-align: middle;
    }
    & > img {
      margin-right: 12px;
      cursor: pointer;
    }
  }
  h6 {
    margin-bottom: 14px;
    color: $font-color-content;
  }
  dl.horizon {
    line-height: 20px;
    dt {
      color: $font-color-desc;
      font-size: 12px;
    }
    dd {
      margin-bottom: 16px;
      font-size: 14px;
    }
    .sub {
      margin-top: 6px;
      font-size: 12px;
    }
  }
  .el-col:first-child {
    padding-right: 6px;
  }
  .el-col:last-child {
    padding-left: 24px;
    h6 {
      margin-bottom: 0;
    }
    dl.horizon dt {
      font-size: 12.6px;
    }
    .price {
      margin: -5px 0 6px;
      padding-top: 0;
      & > span {
        font-size: 20px;
      }
    }
    p {
      margin-top: 20px;
      color: $font-color-desc;
      font-size: 10px;
      line-height: 1.6;
      word-break: normal;
    }
  }
  .el-dialog .el-table {
    margin: 5px 0 20px;
    .el-table__body-wrapper {
      margin: 6px 0;
    }
    .column-plan img {
      width: 28px;
    }
    .el-table__row {
      height: 48px;
    }
    td {
      padding: 0;
    }
  }
  // 카드 전표
  .card-slip {
    dl.horizon dd {
      margin-bottom: 12px;
    }
    .el-button {
      width: calc(50% - 4px);
      height: 36px;
      margin: 0 0 4px 0;
      &:last-child {
        float: right;
      }
    }
  }
}
</style>
