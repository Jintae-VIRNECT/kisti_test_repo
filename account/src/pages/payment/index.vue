<template>
  <div id="payment" class="virnect-card">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>{{ $t('menu.account') }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{ $t('menu.payment') }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('menu.payment') }}</h2>
        <p>{{ $t('payment.desc') }}</p>
      </div>
      <el-row>
        <el-col class="container__left">
          <!-- 결제 예정 정보 -->
          <el-card>
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
              <dd>{{ autoPayments.nextPayDate | dateFormat }}</dd>
              <el-divider />
              <dt>{{ $t('payment.will.way') }}</dt>
              <dd>
                <span>{{ autoPayments.way }}</span>
                <span class="sub">
                  {{ $t('payment.will.autoPaymentEveryMonth') }}
                </span>
              </dd>
            </dl>
            <el-button
              type="simple"
              class="wide"
              @click="showAutoPaymentInfoModal = true"
            >
              {{ $t('common.more') }}
            </el-button>
          </el-card>
          <!-- 결제 수단 등록 & 변경 -->
          <el-card class="way">
            <dl>
              <dt>{{ $t('payment.way.change') }}</dt>
              <dd>
                <p>{{ $t('payment.way.changeDesc') }}</p>
                <a :href="$url.pay">
                  <el-button type="simple" class="wide">
                    {{ $t('common.change') }}
                  </el-button>
                </a>
              </dd>
              <el-divider />
              <dt>{{ $t('payment.way.autoPayment') }}</dt>
              <dd class="auto-payment">
                <span>{{ $t('payment.way.autoPaymentNow') }}</span>
                <el-button
                  type="simple"
                  @click="showAutoPaymentCancelModal = true"
                >
                  {{ $t('payment.way.autoPaymentCancel') }}
                </el-button>
              </dd>
            </dl>
          </el-card>
        </el-col>
        <!-- 결제 이력 -->
        <el-col class="container__right">
          <el-card class="el-card--table">
            <div slot="header">
              <h3>{{ $t('payment.log.title') }}</h3>
            </div>
            <el-table
              ref="table"
              :data="paymentLogs"
              class="clickable"
              @row-click="showLogDetail"
            >
              <column-default :label="$t('payment.log.column.no')" prop="no" />
              <column-default
                :label="$t('payment.log.column.way')"
                prop="way"
                :width="200"
              />
              <column-price
                :label="$t('payment.log.column.price')"
                prop="price"
                :width="140"
              />
              <column-date
                :label="$t('payment.log.column.paidDate')"
                prop="paidDate"
                :width="100"
              />
            </el-table>
            <searchbar-page
              ref="page"
              :value.sync="paymentLogsPage"
              :total="paymentLogsTotal"
            />
          </el-card>
        </el-col>
      </el-row>
    </div>
    <!-- 모달 -->
    <auto-payment-info-modal
      :autoPaymentItems="autoPayments.items"
      :visible.sync="showAutoPaymentInfoModal"
    />
    <auto-payment-cancel-modal
      :autoPaymentId="autoPayments.id"
      :autoPaymentItems="autoPayments.items"
      :visible.sync="showAutoPaymentCancelModal"
    />
    <!-- <payment-log-detail-modal
      :logInfo="activeLog"
      :visible.sync="showPaymentLogDetailModal"
    /> -->
  </div>
</template>

<script>
import columnMixin from '@/mixins/columns'
import filterMixin from '@/mixins/filters'
import searchMixin from '@/mixins/search'
import paymentService from '@/services/payment'
import AutoPaymentInfoModal from '@/components/payment/AutoPaymentInfoModal'
import AutoPaymentCancelModal from '@/components/payment/AutoPaymentCancelModal'
// import PaymentLogDetailModal from '@/components/payment/PaymentLogDetailModal'

export default {
  mixins: [columnMixin, filterMixin, searchMixin],
  components: {
    AutoPaymentInfoModal,
    AutoPaymentCancelModal,
    // PaymentLogDetailModal,
  },
  async asyncData() {
    const promises = {
      autoPayments: paymentService.getAutoPayments(),
      logs: paymentService.searchPaymentLogs(),
    }
    return {
      autoPayments: await promises.autoPayments,
      paymentLogs: (await promises.logs).list,
      paymentLogsTotal: (await promises.logs).total,
    }
  },
  data() {
    return {
      paymentLogsPage: 1,
      activeLog: null,
      showAutoPaymentInfoModal: false,
      showAutoPaymentCancelModal: false,
      showPaymentLogDetailModal: false,
    }
  },
  methods: {
    async changedSearchParams(searchParams) {
      const { list, total } = await paymentService.searchPaymentLogs(
        searchParams,
      )
      this.paymentLogs = list
      this.paymentLogsTotal = total
    },
    async showLogDetail(log) {
      // this.activeLog = log
      // this.showPaymentLogDetailModal = true
      const { slipLink } = await paymentService.getPaymentLogDetail(log.no)
      if (slipLink) window.open(slipLink)
    },
  },
}
</script>

<style lang="scss">
#payment {
  .el-button.wide {
    width: 100%;
    height: 36px;
    font-size: 14px;
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
    .auto-payment {
      margin: 14px 0 8px;
      .el-button {
        float: right;
        margin-top: -5px;
      }
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
}
</style>
