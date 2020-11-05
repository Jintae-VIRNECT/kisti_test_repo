import Model from '@/models/Model'
import Ticket from './Ticket'
import products from '@/models/products'
import { Plan } from '@/models/purchases/PlansInfo'

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

    this.products = [
      new Plan(products.remote),
      new Plan(products.make),
      new Plan(products.view),
    ]
    this.storage = {
      default: 0,
      add: 0,
    }
    this.viewCount = {
      default: 100000,
      add: 0,
    }

    // 현재 자동 결제 중일 때만
    if (json.PayFlag !== 'Y') {
      this.price = 0
      this.nextPayDate = null
      this.items = []
      this.viewCount.default = 0
    }

    this.items.forEach(item => {
      const product = this.products.find(
        p => p.value === item.productType.name.toLowerCase(),
      )
      if (product) product.amount += item.count
      if (item.productType.id === 'product') {
        this.storage.default += item.storage
        this.viewCount.default += item.viewCount
      }
      if (item.productType.id === 'service') {
        this.storage.add += item.storage
        this.viewCount.add += item.viewCount
      }
    })
  }
}
