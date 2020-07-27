import Model from '@/models/Model'
import Ticket from '@/models/payment/Ticket'

export default class PayemntLogDetail extends Model {
  constructor(json) {
    super()
    this.no = 1
    this.way = '신용카드'
    this.price = 100000
    this.paidDate = new Date()
    // this.tickets = [new Ticket(), new Ticket()]
    this.vat = 100000
    this.discount = 100000
    this.startDate = new Date()
    this.endDate = new Date()
    this.total = 100000000

    this.slipLink = json.ReceiptLink
    this.tickets = json.purchase.map(p => new Ticket(p))
  }
}
