import Model from '@/models/Model'

export default class SearchParams extends Model {
  constructor(json) {
    super()
    this.filter = json.filter || {
      targetFilter: 'ALL',
      modeFilter: 'ALL',
      sharedTypes: 'ALL',
      editTypes: 'ALL',
    }
    this.search = json.search || ''
    this.page = json.page || 1
    this.sort = json.sort || 'createdDate,desc'
  }
}
