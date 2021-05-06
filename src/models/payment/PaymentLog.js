import Model from '@/models/Model'

export default class PayemntLog extends Model {
  constructor(json) {
    super()
    this.no = json.CashNo
    this.way = json.PGName
    this.price = json.PayAmt
    this.priceUnit = json.CurrencyCode
    this.paidDate = json.PayYMD
  }
}
