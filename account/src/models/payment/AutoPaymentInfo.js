import Model from '@/models/Model'
import Ticket from './Ticket'
import filters from '@/mixins/filters'

export default class AutoPaymentInfo extends Model {
  constructor(json) {
    super()
    this.id = json.MSeqNo
    this.way = json.PGName
    this.price = json.TotalPayAmt
    this.priceUnit = json.CurrencyCode
    this.nextPayDate = json.NextPayDate
    this.payFlag = json.PayFlag
    this.items = json.products.map(product => new Ticket(product))

    // 자동 결제 해지 신청중
    if (json.PayFlag === 'N') {
      this.price = 0
      this.nextPayDate = null
      this.items = []
    }
  }
}
