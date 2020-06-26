import Ticket from '@/models/payment/Ticket'
import PaymentLog from '@/models/payment/PaymentLog'
import PaymentLogDetail from '@/models/payment/PaymentLogDetail'
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
  getPaymentLogDetail() {
    return new PaymentLogDetail()
  },
  getAutoPayments() {
    const data = [0, 1, 2]
    return data.map(() => new Ticket())
  },
}
