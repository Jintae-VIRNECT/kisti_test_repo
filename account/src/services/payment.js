import PaymentLog from '@/models/payment/PaymentLog'

export default {
  searchPaymentLogs() {
    const data = [0, 1, 2, 3, 4]
    return {
      list: data.map(() => new PaymentLog()),
      total: 30,
    }
  },
}
