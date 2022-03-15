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
          <PaymentInfo />
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
              <ColumnDefault :label="$t('payment.log.column.no')" prop="no" />
              <ColumnDefault
                :label="$t('payment.log.column.way')"
                prop="way"
                :width="200"
              />
              <ColumnPrice
                :label="$t('payment.log.column.price')"
                prop="price"
                :width="140"
              />
              <ColumnDate
                :label="$t('payment.log.column.paidDate')"
                prop="paidDate"
                :width="120"
              />
            </el-table>
            <SearchbarPage
              ref="page"
              :value.sync="paymentLogsPage"
              :total="paymentLogsTotal"
            />
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import columnMixin from '@/mixins/columns'
import searchMixin from '@/mixins/search'
import paymentService from '@/services/payment'

export default {
  mixins: [columnMixin, searchMixin],
  async asyncData() {
    const logs = await paymentService.searchPaymentLogs()
    return {
      paymentLogs: logs.list,
      paymentLogsTotal: logs.total,
    }
  },
  data() {
    return {
      paymentLogsPage: 1,
      activeLog: null,
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
      const { slipLink } = await paymentService.getPaymentLogDetail(log.no)
      if (slipLink) window.open(slipLink)
    },
  },
}
</script>
