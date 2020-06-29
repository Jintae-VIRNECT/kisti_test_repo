import Model from '@/models/Model'

export default class Ticket extends Model {
  constructor(json) {
    super()
    this.id = json.ItemID
    this.product = null
    this.name = json.ItemName || json.ProductName
    this.desc = json.ItemDesc
    this.count = json.ItemCnt || json.ProductCnt
    this.price = json.PayAmt
    this.priceUnit = json.CurrencyCode
  }
}
