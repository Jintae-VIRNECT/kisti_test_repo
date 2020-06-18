import Ticket from '@/models/payment/Ticket'
import PaymentLog from '@/models/payment/PaymentLog'
import PaymentLogDetail from '@/models/payment/PaymentLogDetail'

export default {
  searchPaymentLogs() {
    const data = [0, 1, 2, 3, 4]
    return {
      list: data.map(() => new PaymentLog()),
      total: 30,
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
