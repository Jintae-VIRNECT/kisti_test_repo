import Model from '@/models/Model'
import Ticket from './Ticket'

export default class AutoPaymentInfo extends Model {
  constructor(json) {
    super()
    this.id = json.MSeqNo
    this.way = json.PGName
    this.price = json.TotalPayAmt
    this.priceUnit = json.CurrencyCode
    this.nextPayDate = json.NextPayDate
    this.nextPayDate = json.NextPayDate
    this.payFlag = json.PayFlag
    this.items = json.products.map(product => new Ticket(product))
  }
}
