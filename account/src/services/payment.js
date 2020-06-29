import PaymentLog from '@/models/payment/PaymentLog'
import PaymentLogDetail from '@/models/payment/PaymentLogDetail'
import AutoPaymentInfo from '@/models/payment/AutoPaymentInfo'
import { api } from '@/plugins/axios'

export default {
  async searchPaymentLogs(searchParams = {}) {
    const data = await api('GET_PAYMENT_LOGS', {
      params: {
        userno: 71,
        fromymd: '2020-01-01',
        toymd: '2999-01-01',
        pagesize: 10,
        pageno: searchParams.page || 1,
      },
    })
    return {
      list: data.payments.map(log => new PaymentLog(log)),
      total: data.totalcnt,
    }
  },
  async getPaymentLogDetail(no) {
    const data = await api('GET_PAYMENT_LOG_DETAIL', {
      params: {
        userno: 71,
        cashno: no,
      },
    })
    return new PaymentLogDetail(data)
  },
  async getAutoPayments() {
    const data = await api('GET_AUTO_PAYMENTS', {
      params: { userno: 71 },
    })
    return new AutoPaymentInfo(data)
  },
}
