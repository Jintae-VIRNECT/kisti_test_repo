import { shallowMount } from '@vue/test-utils'
import CollaboStatus from 'components/modules/CollaboStatus'
import localVue from '__utils__/localVue'

import { collabo } from 'utils/collabo'

describe('CollaboStatus', () => {
  it('커스텀 스타일 전달', () => {
    const wrapper = shallowMount(CollaboStatus, {
      localVue,
      propsData: { customClass: 'customClass' },
    })

    expect(wrapper.find('.customClass').exists(true)).toEqual(true)
  })

  it('FINISHED progress class 렌더링', () => {
    const wrapper = shallowMount(CollaboStatus, {
      localVue,
      propsData: { status: collabo.FINISHED },
    })

    expect(wrapper.find('.finished').exists(true)).toEqual(true)
  })
})
