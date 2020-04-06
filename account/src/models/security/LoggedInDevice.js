import Model from '@/models/Model'

export default class LoggedInDevice extends Model {
  constructor() {
    super()
    this.name = 'Virnect - [Device 01]'
    this.location = '대한민국 - 서울 특별시'
    this.loginDate = '21.12.30'
  }
}
