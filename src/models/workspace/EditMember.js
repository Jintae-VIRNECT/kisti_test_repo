import Model from '@/models/Model'
import plans from '@/models/workspace/plans'

export default class EditMember extends Model {
  /**
   * 멤버 수정 json 구조
   * @param {Member} member
   */
  constructor(member) {
    super()
    this.userId = member.uuid
    this.role = member.role
    this.licenseRemote = member.licenseProducts.includes(plans.remote.value)
    this.licenseMake = member.licenseProducts.includes(plans.make.value)
    this.licenseView = member.licenseProducts.includes(plans.view.value)
  }
}
