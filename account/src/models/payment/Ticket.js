import Model from '@/models/Model'
import products from '@/models/products'

export default class Ticket extends Model {
  constructor(json) {
    super()
    this.id = json.ItemID
    this.name = json.ItemName || json.ProductName
    this.desc = json.ItemDesc
    this.count = json.ItemCnt || json.ProductCnt
    this.price = json.PayAmt
    this.priceUnit = json.CurrencyCode
    this.callTime = json.productCallTime
    this.viewCount = json.productHit
    this.storage = json.productStorage
    this.productType = json.productType

    const product = Object.values(products).find(({ id }) => id === json.ItemID)
    this.product = product ? product.value : null
  }
}
