import PaymentLog from '@/models/payment/PaymentLog'
import PaymentLogDetail from '@/models/payment/PaymentLogDetail'
import AutoPaymentInfo from '@/models/payment/AutoPaymentInfo'
import { api } from '@/plugins/axios'
import { CANCEL_AUTO_PAYMENTS } from '../api/uri'

export default {
  /**
   * 결제정보 목록 검색
   * @param {Object} searchParams
   */
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
  /**
   * 결제상세정보
   * @param {String} no
   */
  async getPaymentLogDetail(no) {
    const data = await api('GET_PAYMENT_LOG_DETAIL', {
      params: {
        userno: 71,
        cashno: no,
      },
    })
    return new PaymentLogDetail(data)
  },
  /**
   * 내 정기결제 정보 가져오기
   */
  async getAutoPayments() {
    const data = await api('GET_AUTO_PAYMENTS', {
      params: { userno: 71 },
    })
    return new AutoPaymentInfo(data)
  },
  /**
   * 정기결제 해지하기
   */
  async cancelAutoPayments(no) {
    const data = await api('CANCEL_AUTO_PAYMENTS', {
      params: {
        userno: 71,
        MSeqNo: no,
      },
    })
    return data
  },
}
