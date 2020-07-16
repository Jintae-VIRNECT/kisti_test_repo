import Model from '@/models/Model'
import Ticket from './Ticket'
import filters from '@/mixins/filters'

const { mb2gb } = filters.methods

function sumAvailable(items) {
  const available = items.reduce(
    (p, n) => {
      p.storage += n.storage
      p.viewCount += n.viewCount
      p.callTime += n.callTime
      return p
    },
    { storage: 0, viewCount: 0, callTime: 0 },
  )
  available.storage = mb2gb(available.storage)
  return available
}

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
    this.maxAvailable = sumAvailable(this.items)
    this.basisAvailable = sumAvailable(
      this.items.filter(item => item.productType.name === 'product'),
    )
    this.extendAvailable = sumAvailable(
      this.items.filter(item => item.productType.name === 'service'),
    )

    // 자동 결제 해지 신청중
    if (json.PayFlag === 'N') {
      this.price = 0
      this.nextPayDate = null
      this.items = []
    }
  }
}
